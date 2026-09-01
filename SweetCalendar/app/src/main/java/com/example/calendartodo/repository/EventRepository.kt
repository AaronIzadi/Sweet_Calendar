package com.example.calendartodo.repository

import com.example.calendartodo.data.local.EventCacheDao
import com.example.calendartodo.data.local.EventCacheEntity
import com.example.calendartodo.data.remote.HolidayApiService
import com.example.calendartodo.jalali.JalaliDate
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val dao: EventCacheDao,
    private val api: HolidayApiService
) {

    fun observeForMonth(jalaliYear: Int, jalaliMonth: Int): Flow<List<EventCacheEntity>> =
        dao.observeForMonth("%04d-%02d".format(jalaliYear, jalaliMonth))

    fun observeForDate(jalaliDate: String): Flow<List<EventCacheEntity>> =
        dao.observeForDate(jalaliDate)

    /**
     * Ensures the given Jalali month's events are cached locally, fetching
     * only the days that aren't already cached. Safe to call every time the
     * calendar screen opens a month — it's a no-op once cached.
     */
    suspend fun ensureMonthCached(jalaliYear: Int, jalaliMonth: Int) = coroutineScope {
        val prefix = "%04d-%02d".format(jalaliYear, jalaliMonth)
        val alreadyCached = dao.cachedDatesForMonth(prefix).toSet()
        val daysInMonth = JalaliDate(jalaliYear, jalaliMonth, 1).daysInMonth()

        val missingDays = (1..daysInMonth).filter { day ->
            "$prefix-%02d".format(day) !in alreadyCached
        }
        if (missingDays.isEmpty()) return@coroutineScope

        // Fetch missing days concurrently; failures for individual days are
        // swallowed so one bad day doesn't block the whole month.
        val deferred = missingDays.map { day ->
            async {
                runCatching { day to api.getJalaliDayEvents(jalaliYear, jalaliMonth, day) }.getOrNull()
            }
        }

        val entities = deferred.mapNotNull { it.await() }.flatMap { (day, response) ->
            val dateStr = "$prefix-%02d".format(day)
            if (response.events.isEmpty()) {
                // Sentinel row so an event-free day still counts as "cached"
                // and isn't re-fetched every time the month is opened.
                listOf(EventCacheEntity(jalaliDate = dateStr, description = "", isHoliday = false))
            } else {
                response.events.map { evt ->
                    EventCacheEntity(
                        jalaliDate = dateStr,
                        description = evt.description,
                        additionalDescription = evt.additional_description.orEmpty(),
                        isHoliday = evt.is_holiday
                    )
                }
            }
        }

        if (entities.isNotEmpty()) dao.insertAll(entities)
    }
}

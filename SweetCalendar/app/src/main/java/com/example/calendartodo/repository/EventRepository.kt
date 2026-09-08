package com.example.calendartodo.repository

import com.example.calendartodo.data.local.EventCacheDao
import com.example.calendartodo.data.local.EventCacheEntity
import com.example.calendartodo.data.local.EventMonthCacheDao
import com.example.calendartodo.data.local.EventMonthCacheEntity
import com.example.calendartodo.data.remote.TimeIrCalendarClient
import com.example.calendartodo.data.remote.TimeIrEventDto
import com.example.calendartodo.jalali.GregorianDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class EventRepository(
    private val dao: EventCacheDao,
    private val monthCacheDao: EventMonthCacheDao,
    private val timeIrClient: TimeIrCalendarClient
) {

    fun observeForMonth(jalaliYear: Int, jalaliMonth: Int): Flow<List<EventCacheEntity>> =
        dao.observeForMonth("%04d-%02d".format(jalaliYear, jalaliMonth))

    fun observeForDate(jalaliDate: String): Flow<List<EventCacheEntity>> =
        dao.observeForDate(jalaliDate)

    fun observeForGregorianMonth(gregorianYear: Int, gregorianMonth: Int): Flow<List<EventCacheEntity>> {
        val jalaliMonths = GregorianDate.jalaliMonthsOverlapping(gregorianYear, gregorianMonth)
        if (jalaliMonths.isEmpty()) return observeForMonth(gregorianYear, gregorianMonth)
        val flows = jalaliMonths.map { (year, month) -> observeForMonth(year, month) }
        return combine(flows) { chunks -> chunks.flatMap { it.toList() } }
    }

    suspend fun ensureGregorianMonthCached(
        gregorianYear: Int,
        gregorianMonth: Int,
        forceRefresh: Boolean = false
    ) {
        GregorianDate.jalaliMonthsOverlapping(gregorianYear, gregorianMonth).forEach { (year, month) ->
            ensureMonthCached(year, month, forceRefresh)
        }
    }

    /**
     * Ensures the given Persian month's occasions are cached locally.
     * Fetches the full month from time.ir in a single request (same as cm-calendar-service).
     */
    suspend fun ensureMonthCached(jalaliYear: Int, jalaliMonth: Int, forceRefresh: Boolean = false) {
        val yearMonth = "%04d-%02d".format(jalaliYear, jalaliMonth)
        if (!forceRefresh && monthCacheDao.isCached(yearMonth)) return
        if (forceRefresh) {
            monthCacheDao.delete(yearMonth)
        }

        val calendar = timeIrClient.fetchMonth(jalaliYear, jalaliMonth)

        val entities = calendar.eventList
            .mapNotNull { it.toCacheEntityOrNull() }
            .distinctBy { Pair(it.sourceEventId, it.gregorianDateKey) }
            .map { it.entity }

        dao.deleteForMonth(yearMonth)
        if (entities.isNotEmpty()) {
            dao.insertAll(entities)
        }

        val sourceVersion = buildSourceVersion(jalaliYear, calendar.createdDate)
        monthCacheDao.insert(
            EventMonthCacheEntity(
                yearMonth = yearMonth,
                sourceVersion = sourceVersion
            )
        )
    }

    /**
     * Imports all twelve months for a Persian year (used when prefetching a full year).
     */
    suspend fun ensureYearCached(jalaliYear: Int, forceRefresh: Boolean = false) {
        for (month in 1..12) {
            ensureMonthCached(jalaliYear, month, forceRefresh)
        }
    }

    private data class MappedEvent(
        val sourceEventId: Int,
        val gregorianDateKey: String,
        val entity: EventCacheEntity
    )

    private fun TimeIrEventDto.toCacheEntityOrNull(): MappedEvent? {
        if (title.isBlank()) return null
        if (gregorianYear <= 0 || gregorianMonth !in 1..12 || gregorianDay !in 1..31) return null
        if (jalaliYear <= 0 || jalaliMonth !in 1..12 || jalaliDay !in 1..31) return null

        val gregorianKey = "%04d-%02d-%02d".format(gregorianYear, gregorianMonth, gregorianDay)
        return MappedEvent(
            sourceEventId = id,
            gregorianDateKey = gregorianKey,
            entity = EventCacheEntity(
                jalaliDate = jalaliDateKey(),
                description = title.trim(),
                additionalDescription = body.orEmpty().trim(),
                isHoliday = isHoliday
            )
        )
    }

    private fun TimeIrEventDto.jalaliDateKey(): String =
        "%04d-%02d-%02d".format(jalaliYear, jalaliMonth, jalaliDay)

    private fun buildSourceVersion(persianYear: Int, createdDate: String?): String {
        val stamp = createdDate?.filter { it.isDigit() }?.take(14).orEmpty()
        return if (stamp.isEmpty()) {
            "timeir-$persianYear-unknown"
        } else {
            "timeir-$persianYear-$stamp"
        }
    }
}

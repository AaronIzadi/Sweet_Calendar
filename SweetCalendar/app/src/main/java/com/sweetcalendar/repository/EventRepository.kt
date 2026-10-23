package com.sweetcalendar.repository

import com.sweetcalendar.data.local.EventCacheDao
import com.sweetcalendar.data.local.EventCacheEntity
import com.sweetcalendar.data.local.EventMonthCacheDao
import com.sweetcalendar.data.local.EventMonthCacheEntity
import com.sweetcalendar.data.remote.NagerHolidayDto
import com.sweetcalendar.data.remote.NagerHolidaysClient
import com.sweetcalendar.data.remote.TimeIrCalendarClient
import com.sweetcalendar.data.remote.TimeIrEventDto
import com.sweetcalendar.jalali.GregorianDate
import com.sweetcalendar.jalali.JalaliDate
import kotlinx.coroutines.flow.Flow

class EventRepository(
    private val dao: EventCacheDao,
    private val monthCacheDao: EventMonthCacheDao,
    private val timeIrClient: TimeIrCalendarClient,
    private val nagerClient: NagerHolidaysClient,
    private val holidayCountryCode: () -> String,
) {

    fun observeForMonth(jalaliYear: Int, jalaliMonth: Int): Flow<List<EventCacheEntity>> =
        dao.observeForMonth(
            "%04d-%02d".format(jalaliYear, jalaliMonth),
            EventCacheEntity.SOURCE_TIME_IR,
        )

    fun observeForDate(jalaliDate: String, source: String): Flow<List<EventCacheEntity>> =
        dao.observeForDate(jalaliDate, source)

    fun observeForGregorianDate(gregorianDate: String): Flow<List<EventCacheEntity>> =
        dao.observeForGregorianDate(gregorianDate, EventCacheEntity.SOURCE_NAGER)

    fun observeForGregorianMonth(gregorianYear: Int, gregorianMonth: Int): Flow<List<EventCacheEntity>> =
        dao.observeForGregorianMonth(
            "%04d-%02d".format(gregorianYear, gregorianMonth),
            EventCacheEntity.SOURCE_NAGER,
        )

    suspend fun ensureGregorianMonthCached(
        gregorianYear: Int,
        gregorianMonth: Int,
        forceRefresh: Boolean = false,
    ) {
        ensureGregorianYearCached(gregorianYear, forceRefresh)
    }

    /**
     * Ensures the given Persian month's occasions are cached locally (time.ir).
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

        dao.deleteForMonth(yearMonth, EventCacheEntity.SOURCE_TIME_IR)
        if (entities.isNotEmpty()) {
            dao.insertAll(entities)
        }

        val sourceVersion = buildTimeIrSourceVersion(jalaliYear, calendar.createdDate)
        monthCacheDao.insert(
            EventMonthCacheEntity(
                yearMonth = yearMonth,
                sourceVersion = sourceVersion,
            )
        )
    }

    private suspend fun ensureGregorianYearCached(gregorianYear: Int, forceRefresh: Boolean) {
        val country = resolveCountryCode()
        val cacheKey = "GY-$gregorianYear-$country"
        if (!forceRefresh && monthCacheDao.isCached(cacheKey)) return
        if (forceRefresh) {
            monthCacheDao.delete(cacheKey)
        }

        val holidays = nagerClient.fetchYear(gregorianYear, country)
        val entities = holidays
            .mapNotNull { it.toCacheEntityOrNull() }
            .distinctBy { "${it.jalaliDate}|${it.description}" }

        dao.deleteForGregorianYear("$gregorianYear-", EventCacheEntity.SOURCE_NAGER)
        if (entities.isNotEmpty()) {
            dao.insertAll(entities)
        }

        monthCacheDao.insert(
            EventMonthCacheEntity(
                yearMonth = cacheKey,
                sourceVersion = "nager-$gregorianYear-$country",
            )
        )
    }

    suspend fun ensureYearCached(jalaliYear: Int, forceRefresh: Boolean = false) {
        for (month in 1..12) {
            ensureMonthCached(jalaliYear, month, forceRefresh)
        }
    }

    private fun resolveCountryCode(): String {
        val raw = holidayCountryCode().trim().uppercase()
        return if (raw.length == 2) raw else DEFAULT_COUNTRY_CODE
    }

    private data class MappedEvent(
        val sourceEventId: Int,
        val gregorianDateKey: String,
        val entity: EventCacheEntity,
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
                isHoliday = isHoliday,
                gregorianDate = gregorianKey,
                source = EventCacheEntity.SOURCE_TIME_IR,
            )
        )
    }

    private fun NagerHolidayDto.toCacheEntityOrNull(): EventCacheEntity? {
        if (date.isBlank()) return null
        val parts = date.split("-")
        if (parts.size != 3) return null
        val gy = parts[0].toIntOrNull() ?: return null
        val gm = parts[1].toIntOrNull() ?: return null
        val gd = parts[2].toIntOrNull() ?: return null
        if (gm !in 1..12 || gd !in 1..31) return null

        val jalali = GregorianDate(gy, gm, gd).toJalali()
        val jalaliKey = "%04d-%02d-%02d".format(jalali.year, jalali.month, jalali.day)
        val title = localName.ifBlank { name }.trim()
        if (title.isBlank()) return null

        return EventCacheEntity(
            jalaliDate = jalaliKey,
            description = title,
            additionalDescription = name.trim(),
            isHoliday = types.any { type ->
                type.equals("Public", ignoreCase = true) || type.equals("Bank", ignoreCase = true)
            },
            gregorianDate = date,
            source = EventCacheEntity.SOURCE_NAGER,
        )
    }

    private fun TimeIrEventDto.jalaliDateKey(): String =
        "%04d-%02d-%02d".format(jalaliYear, jalaliMonth, jalaliDay)

    private fun buildTimeIrSourceVersion(persianYear: Int, createdDate: String?): String {
        val stamp = createdDate?.filter { it.isDigit() }?.take(14).orEmpty()
        return if (stamp.isEmpty()) {
            "timeir-$persianYear-unknown"
        } else {
            "timeir-$persianYear-$stamp"
        }
    }

    private companion object {
        const val DEFAULT_COUNTRY_CODE = "US"
    }
}

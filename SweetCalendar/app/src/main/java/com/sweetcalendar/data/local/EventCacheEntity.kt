package com.sweetcalendar.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * A cached official/occasion event for a given Jalali day, sourced from time.ir.
 * Months are fetched in one API call and stored locally.
 */
@Entity(tableName = "event_cache", primaryKeys = ["jalaliDate", "description"])
data class EventCacheEntity(
    val jalaliDate: String, // "1403-05-12"
    val description: String,
    val additionalDescription: String = "",
    val isHoliday: Boolean,
    /** yyyy-MM-dd when sourced from Nager / time.ir Gregorian fields. */
    val gregorianDate: String = "",
    val source: String = SOURCE_TIME_IR,
) {
    companion object {
        const val SOURCE_TIME_IR = "timeir"
        const val SOURCE_NAGER = "nager"
    }
}

@Dao
interface EventCacheDao {

    @Query(
        """
        SELECT * FROM event_cache
        WHERE jalaliDate LIKE :yearMonthPrefix || '%' AND source = :source
        """
    )
    fun observeForMonth(yearMonthPrefix: String, source: String): Flow<List<EventCacheEntity>>

    @Query(
        """
        SELECT * FROM event_cache
        WHERE gregorianDate LIKE :yearMonthPrefix || '%' AND source = :source
        """
    )
    fun observeForGregorianMonth(yearMonthPrefix: String, source: String): Flow<List<EventCacheEntity>>

    @Query("SELECT * FROM event_cache WHERE jalaliDate = :jalaliDate AND source = :source")
    fun observeForDate(jalaliDate: String, source: String): Flow<List<EventCacheEntity>>

    @Query("SELECT * FROM event_cache WHERE gregorianDate = :gregorianDate AND source = :source")
    fun observeForGregorianDate(gregorianDate: String, source: String): Flow<List<EventCacheEntity>>

    @Query("DELETE FROM event_cache WHERE gregorianDate LIKE :yearPrefix || '%' AND source = :source")
    suspend fun deleteForGregorianYear(yearPrefix: String, source: String)

    @Query("SELECT DISTINCT jalaliDate FROM event_cache WHERE jalaliDate LIKE :yearMonthPrefix || '%'")
    suspend fun cachedDatesForMonth(yearMonthPrefix: String): List<String>

    @Query(
        """
        DELETE FROM event_cache
        WHERE jalaliDate LIKE :yearMonthPrefix || '%' AND source = :source
        """
    )
    suspend fun deleteForMonth(yearMonthPrefix: String, source: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventCacheEntity>)
}

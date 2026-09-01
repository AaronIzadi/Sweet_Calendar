package com.example.calendartodo.data.local

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
    val isHoliday: Boolean
)

@Dao
interface EventCacheDao {

    @Query("SELECT * FROM event_cache WHERE jalaliDate LIKE :yearMonthPrefix || '%'")
    fun observeForMonth(yearMonthPrefix: String): Flow<List<EventCacheEntity>>

    @Query("SELECT * FROM event_cache WHERE jalaliDate = :jalaliDate")
    fun observeForDate(jalaliDate: String): Flow<List<EventCacheEntity>>

    @Query("SELECT DISTINCT jalaliDate FROM event_cache WHERE jalaliDate LIKE :yearMonthPrefix || '%'")
    suspend fun cachedDatesForMonth(yearMonthPrefix: String): List<String>

    @Query("DELETE FROM event_cache WHERE jalaliDate LIKE :yearMonthPrefix || '%'")
    suspend fun deleteForMonth(yearMonthPrefix: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(events: List<EventCacheEntity>)
}

package com.example.calendartodo.data.local

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "event_month_cache")
data class EventMonthCacheEntity(
    @PrimaryKey val yearMonth: String,
    val sourceVersion: String = "",
    val importedAt: Long = System.currentTimeMillis()
)

@Dao
interface EventMonthCacheDao {

    @Query("SELECT EXISTS(SELECT 1 FROM event_month_cache WHERE yearMonth = :yearMonth)")
    suspend fun isCached(yearMonth: String): Boolean

    @Query("DELETE FROM event_month_cache WHERE yearMonth = :yearMonth")
    suspend fun delete(yearMonth: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: EventMonthCacheEntity)
}

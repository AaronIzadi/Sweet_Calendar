package com.sweetcalendar.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [TaskEntity::class, EventCacheEntity::class, EventMonthCacheEntity::class],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun eventCacheDao(): EventCacheDao
    abstract fun eventMonthCacheDao(): EventMonthCacheDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN reminderTime TEXT DEFAULT NULL")
                db.execSQL("ALTER TABLE tasks ADD COLUMN category TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN priority TEXT NOT NULL DEFAULT 'Medium'")
                db.execSQL("ALTER TABLE tasks ADD COLUMN repeatWeekly INTEGER NOT NULL DEFAULT 0")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE event_cache ADD COLUMN additionalDescription TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS event_month_cache (
                        yearMonth TEXT NOT NULL PRIMARY KEY,
                        sourceVersion TEXT NOT NULL,
                        importedAt INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN deletedAt INTEGER DEFAULT NULL")
            }
        }

        private val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN completedAt INTEGER DEFAULT NULL")
                db.execSQL(
                    "UPDATE tasks SET completedAt = createdAt WHERE isDone = 1 AND completedAt IS NULL"
                )
            }
        }

        private val MIGRATION_7_8 = object : Migration(7, 8) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE event_cache ADD COLUMN gregorianDate TEXT NOT NULL DEFAULT ''"
                )
                db.execSQL(
                    "ALTER TABLE event_cache ADD COLUMN source TEXT NOT NULL DEFAULT 'timeir'"
                )
            }
        }

        private val MIGRATION_10_11 = object : Migration(10, 11) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "ALTER TABLE tasks ADD COLUMN recurrenceWeekdays TEXT NOT NULL DEFAULT ''"
                )
            }
        }

        private val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN recurrenceCount INTEGER DEFAULT NULL")
            }
        }

        private val MIGRATION_8_9 = object : Migration(8, 9) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE tasks ADD COLUMN seriesId TEXT DEFAULT NULL")
                db.execSQL(
                    "ALTER TABLE tasks ADD COLUMN recurrenceFreq TEXT NOT NULL DEFAULT 'NONE'"
                )
                db.execSQL(
                    "ALTER TABLE tasks ADD COLUMN recurrenceInterval INTEGER NOT NULL DEFAULT 1"
                )
                db.execSQL("ALTER TABLE tasks ADD COLUMN seriesUntil TEXT DEFAULT NULL")
                db.execSQL(
                    "ALTER TABLE tasks ADD COLUMN isRecurrenceException INTEGER NOT NULL DEFAULT 0"
                )
                db.execSQL(
                    "UPDATE tasks SET recurrenceFreq = 'WEEKLY' WHERE repeatWeekly = 1"
                )
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "calendar_todo.db"
                )
                    .addMigrations(
                        MIGRATION_1_2,
                        MIGRATION_2_3,
                        MIGRATION_3_4,
                        MIGRATION_4_5,
                        MIGRATION_5_6,
                        MIGRATION_6_7,
                        MIGRATION_7_8,
                        MIGRATION_8_9,
                        MIGRATION_9_10,
                        MIGRATION_10_11,
                    )
                    .build()
                    .also { INSTANCE = it }
            }
    }
}

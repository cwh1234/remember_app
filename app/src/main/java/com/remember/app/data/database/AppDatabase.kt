package com.remember.app.data.database

import android.content.Context
import android.util.Log
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.remember.app.data.database.dao.HolidayDao
import com.remember.app.data.database.dao.ReminderDao
import com.remember.app.data.database.entity.Holiday
import com.remember.app.data.database.entity.Reminder
import java.util.Calendar

@Database(
    entities = [Reminder::class, Holiday::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun reminderDao(): ReminderDao
    abstract fun holidayDao(): HolidayDao

    companion object {
        private const val TAG = "AppDatabase"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "remember_app_db"
            )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // 直接在 onCreate 中使用 SupportSQLiteDatabase 预填充节假日
                        prepopulateHolidays(db)
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        Log.w(TAG, "Database was destructively migrated")
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
        }

        /**
         * 使用原始 SQL 预填充节假日，避免依赖 DAO 实例
         */
        private fun prepopulateHolidays(db: SupportSQLiteDatabase) {
            try {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)

                val holidays = listOf(
                    "元旦" to createDate(currentYear, 0, 1),
                    "春节" to createDate(currentYear, 1, 10),
                    "清明节" to createDate(currentYear, 3, 5),
                    "劳动节" to createDate(currentYear, 4, 1),
                    "端午节" to createDate(currentYear, 5, 10),
                    "中秋节" to createDate(currentYear, 8, 15),
                    "国庆节" to createDate(currentYear, 9, 1),
                    "除夕" to createDate(currentYear, 11, 30)
                )

                for ((name, date) in holidays) {
                    db.execSQL(
                        """INSERT OR IGNORE INTO holidays
                           (name, date, is_enabled, is_default)
                           VALUES (?, ?, 1, 1)""",
                        arrayOf(name, date)
                    )
                }
                Log.i(TAG, "Prepopulated ${holidays.size} default holidays")
            } catch (e: Throwable) {
                Log.e(TAG, "Failed to prepopulate holidays", e)
            }
        }

        private fun createDate(year: Int, month: Int, day: Int): Long {
            return Calendar.getInstance().apply {
                set(year, month, day, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
}

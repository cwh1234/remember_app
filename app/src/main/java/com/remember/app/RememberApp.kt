package com.remember.app

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.remember.app.data.database.AppDatabase
import com.remember.app.data.repository.HolidayRepository
import com.remember.app.data.repository.ReminderRepository

class RememberApp : Application() {

    val database by lazy {
        try {
            AppDatabase.getInstance(this)
        } catch (t: Throwable) {
            Log.e("RememberApp", "Failed to create database", t)
            throw t
        }
    }

    val reminderRepository by lazy {
        try {
            ReminderRepository(database.reminderDao())
        } catch (t: Throwable) {
            Log.e("RememberApp", "Failed to create reminderRepo", t)
            throw t
        }
    }

    val holidayRepository by lazy {
        try {
            HolidayRepository(database.holidayDao())
        } catch (t: Throwable) {
            Log.e("RememberApp", "Failed to create holidayRepo", t)
            throw t
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        // 全局异常捕获，防止未处理异常导致闪退
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("RememberApp", "!!! UNCAUGHT EXCEPTION !!!", throwable)
            // 如果是数据库相关错误，尝试重置数据库
            if (throwable.message?.contains("database") == true ||
                throwable.message?.contains("Room") == true ||
                throwable.message?.contains("SQLite") == true
            ) {
                try {
                    deleteDatabase("remember_app_db")
                    Log.w("RememberApp", "Deleted possibly corrupted database")
                } catch (e: Exception) {
                    Log.e("RememberApp", "Failed to delete database", e)
                }
            }
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }

    companion object {
        @Volatile
        private var instance: RememberApp? = null

        fun getInstance(): RememberApp {
            return instance ?: throw IllegalStateException("RememberApp not initialized")
        }
    }
}

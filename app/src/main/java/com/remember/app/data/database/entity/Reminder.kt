package com.remember.app.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 提醒实体
 */
@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "type")
    val type: String, // BIRTHDAY, ANNIVERSARY, BUSINESS_TRIP, DOCUMENT_EXPIRY, BILL_PAYMENT, CUSTOM

    @ColumnInfo(name = "target_date")
    val targetDate: Long, // 目标日期的时间戳（毫秒）

    @ColumnInfo(name = "advance_days")
    val advanceDays: Int = 3, // 提前多少天提醒

    @ColumnInfo(name = "notes")
    val notes: String = "",

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "is_repeat_yearly")
    val isRepeatYearly: Boolean = false, // 是否每年重复

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)

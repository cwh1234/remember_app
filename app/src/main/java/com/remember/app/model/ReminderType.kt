package com.remember.app.model

/**
 * 提醒类型枚举
 */
enum class ReminderType(val displayName: String, val icon: String) {
    BIRTHDAY("生日提醒", "🎂"),
    ANNIVERSARY("纪念日", "💝"),
    BUSINESS_TRIP("出差准备", "✈️"),
    DOCUMENT_EXPIRY("证件到期", "📋"),
    BILL_PAYMENT("账单缴费", "💰"),
    CUSTOM("自定义", "📌");

    companion object {
        fun fromName(name: String): ReminderType {
            return values().find { it.name == name } ?: CUSTOM
        }
    }
}

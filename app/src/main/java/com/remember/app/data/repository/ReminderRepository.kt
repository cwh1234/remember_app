package com.remember.app.data.repository

import com.remember.app.data.database.dao.ReminderDao
import com.remember.app.data.database.entity.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val dao: ReminderDao) {

    fun getAllActiveReminders(): Flow<List<Reminder>> = dao.getAllActiveReminders()

    fun getAllReminders(): Flow<List<Reminder>> = dao.getAllReminders()

    fun getRemindersByType(type: String): Flow<List<Reminder>> = dao.getRemindersByType(type)

    fun getUpcomingReminders(today: Long): Flow<List<Reminder>> = dao.getUpcomingReminders(today)

    fun searchReminders(query: String): Flow<List<Reminder>> = dao.searchReminders(query)

    suspend fun getReminderById(id: Long): Reminder? = dao.getReminderById(id)

    suspend fun insert(reminder: Reminder): Long = dao.insert(reminder)

    suspend fun update(reminder: Reminder) = dao.update(reminder)

    suspend fun delete(reminder: Reminder) = dao.delete(reminder)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun setActive(id: Long, isActive: Boolean) = dao.setActive(id, isActive)
}

package com.remember.app.data.repository

import com.remember.app.data.database.dao.HolidayDao
import com.remember.app.data.database.entity.Holiday
import kotlinx.coroutines.flow.Flow

class HolidayRepository(private val dao: HolidayDao) {

    fun getAllHolidays(): Flow<List<Holiday>> = dao.getAllHolidays()

    fun getEnabledHolidays(): Flow<List<Holiday>> = dao.getEnabledHolidays()

    fun getUpcomingHolidays(today: Long): Flow<List<Holiday>> = dao.getUpcomingHolidays(today)

    suspend fun insert(holiday: Holiday): Long = dao.insert(holiday)

    suspend fun update(holiday: Holiday) = dao.update(holiday)

    suspend fun deleteCustomHoliday(id: Long) = dao.deleteCustomHoliday(id)

    suspend fun setEnabled(id: Long, isEnabled: Boolean) = dao.setEnabled(id, isEnabled)
}

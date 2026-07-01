package com.remember.app.data.database.dao

import androidx.room.*
import com.remember.app.data.database.entity.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {

    /** 获取所有活跃的提醒，按目标日期升序 */
    @Query("SELECT * FROM reminders WHERE is_active = 1 ORDER BY target_date ASC")
    fun getAllActiveReminders(): Flow<List<Reminder>>

    /** 获取所有提醒（包括禁用的） */
    @Query("SELECT * FROM reminders ORDER BY target_date ASC")
    fun getAllReminders(): Flow<List<Reminder>>

    /** 按类型获取提醒 */
    @Query("SELECT * FROM reminders WHERE type = :type AND is_active = 1 ORDER BY target_date ASC")
    fun getRemindersByType(type: String): Flow<List<Reminder>>

    /** 获取从今天起的所有活跃提醒（不含已过期的） */
    @Query("SELECT * FROM reminders WHERE is_active = 1 AND target_date >= :today ORDER BY target_date ASC")
    fun getUpcomingReminders(today: Long): Flow<List<Reminder>>

    /** 按ID获取单个提醒 */
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?

    /** 搜索提醒 */
    @Query("SELECT * FROM reminders WHERE title LIKE '%' || :query || '%' AND is_active = 1 ORDER BY target_date ASC")
    fun searchReminders(query: String): Flow<List<Reminder>>

    /** 插入提醒 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reminder: Reminder): Long

    /** 更新提醒 */
    @Update
    suspend fun update(reminder: Reminder)

    /** 删除提醒 */
    @Delete
    suspend fun delete(reminder: Reminder)

    /** 按ID删除 */
    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteById(id: Long)

    /** 切换提醒的启用状态 */
    @Query("UPDATE reminders SET is_active = :isActive WHERE id = :id")
    suspend fun setActive(id: Long, isActive: Boolean)
}

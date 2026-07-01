package com.remember.app.data.database.dao

import androidx.room.*
import com.remember.app.data.database.entity.Holiday
import kotlinx.coroutines.flow.Flow

@Dao
interface HolidayDao {

    /** 获取所有节假日 */
    @Query("SELECT * FROM holidays ORDER BY date ASC")
    fun getAllHolidays(): Flow<List<Holiday>>

    /** 获取已启用的节假日 */
    @Query("SELECT * FROM holidays WHERE is_enabled = 1 ORDER BY date ASC")
    fun getEnabledHolidays(): Flow<List<Holiday>>

    /** 获取即将到来的节假日 */
    @Query("SELECT * FROM holidays WHERE is_enabled = 1 AND date >= :today ORDER BY date ASC")
    fun getUpcomingHolidays(today: Long): Flow<List<Holiday>>

    /** 插入节假日 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(holiday: Holiday): Long

    /** 批量插入 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(holidays: List<Holiday>)

    /** 更新节假日 */
    @Update
    suspend fun update(holiday: Holiday)

    /** 删除节假日（仅允许删除非默认节假日） */
    @Query("DELETE FROM holidays WHERE id = :id AND is_default = 0")
    suspend fun deleteCustomHoliday(id: Long)

    /** 切换启用状态 */
    @Query("UPDATE holidays SET is_enabled = :isEnabled WHERE id = :id")
    suspend fun setEnabled(id: Long, isEnabled: Boolean)

    /** 检查是否已存在默认节假日 */
    @Query("SELECT COUNT(*) FROM holidays WHERE is_default = 1")
    suspend fun getDefaultHolidayCount(): Int
}

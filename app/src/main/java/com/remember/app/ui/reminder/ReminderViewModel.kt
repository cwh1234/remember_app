package com.remember.app.ui.reminder

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.remember.app.RememberApp
import com.remember.app.data.database.entity.Reminder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class ReminderUiState(
    val upcomingReminders: List<Reminder> = emptyList(),
    val dueReminders: List<Reminder> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null
)

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderRepository = (application as RememberApp).reminderRepository

    private val _uiState = MutableStateFlow(ReminderUiState())
    val uiState: StateFlow<ReminderUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
    }

    private fun loadReminders() {
        val today = getTodayStart()
        val dayInMillis = 86_400_000L
        val todayDays = today / dayInMillis

        viewModelScope.launch(Dispatchers.IO) {
            try {
                reminderRepository.getUpcomingReminders(today)
                    .map { allUpcoming ->
                        // 在 Kotlin 层分离"即将到来"和"最近提醒（已进入窗口期）"
                        val due = allUpcoming.filter { r ->
                            val targetDays = r.targetDate / dayInMillis
                            (targetDays - r.advanceDays) <= todayDays
                        }
                        ReminderUiState(
                            upcomingReminders = allUpcoming,
                            dueReminders = due,
                            isLoading = false
                        )
                    }
                    .catch { e ->
                        Log.e("ReminderVM", "Flow error", e)
                        emit(
                            ReminderUiState(
                                isLoading = false,
                                errorMessage = "加载失败: ${e.message}"
                            )
                        )
                    }
                    .collect { state ->
                        _uiState.value = state
                    }
            } catch (t: Throwable) {
                Log.e("ReminderVM", "Fatal error", t)
                _uiState.value = ReminderUiState(
                    isLoading = false,
                    errorMessage = "初始化失败: ${t.message}"
                )
            }
        }
    }

    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reminderRepository.delete(reminder)
            } catch (t: Throwable) {
                Log.e("ReminderVM", "Delete failed", t)
            }
        }
    }

    fun toggleReminderActive(id: Long, isActive: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                reminderRepository.setActive(id, isActive)
            } catch (t: Throwable) {
                Log.e("ReminderVM", "Toggle failed", t)
            }
        }
    }

    fun getDaysRemaining(targetDate: Long): Int {
        val today = getTodayStart()
        val diff = targetDate - today
        if (diff < 0) return 0
        return (diff / 86_400_000L).toInt()
    }

    private fun getTodayStart(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}

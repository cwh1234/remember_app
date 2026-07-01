package com.remember.app.ui.add

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.remember.app.RememberApp
import com.remember.app.data.database.entity.Reminder
import com.remember.app.model.ReminderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

data class AddUiState(
    val selectedType: ReminderType = ReminderType.BIRTHDAY,
    val title: String = "",
    val targetDate: Long = System.currentTimeMillis(),
    val advanceDays: Int = 3,
    val notes: String = "",
    val isRepeatYearly: Boolean = false,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class AddViewModel(application: Application) : AndroidViewModel(application) {

    private val reminderRepository = try {
        (application as RememberApp).reminderRepository
    } catch (e: Throwable) {
        Log.e("AddVM", "Failed to init repository", e)
        throw e
    }

    private val _uiState = MutableStateFlow(AddUiState())
    val uiState: StateFlow<AddUiState> = _uiState.asStateFlow()

    fun updateType(type: ReminderType) {
        _uiState.value = _uiState.value.copy(selectedType = type)
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(title = title)
    }

    fun updateTargetDate(timestamp: Long) {
        _uiState.value = _uiState.value.copy(targetDate = timestamp)
    }

    fun updateAdvanceDays(days: Int) {
        _uiState.value = _uiState.value.copy(advanceDays = days.coerceIn(0, 90))
    }

    fun updateNotes(notes: String) {
        _uiState.value = _uiState.value.copy(notes = notes)
    }

    fun updateRepeatYearly(repeat: Boolean) {
        _uiState.value = _uiState.value.copy(isRepeatYearly = repeat)
    }

    fun saveReminder() {
        val state = _uiState.value

        if (state.title.isBlank()) {
            _uiState.value = state.copy(errorMessage = "请输入提醒标题")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

            try {
                val reminder = Reminder(
                    title = state.title.trim(),
                    type = state.selectedType.name,
                    targetDate = state.targetDate,
                    advanceDays = state.advanceDays,
                    notes = state.notes.trim(),
                    isRepeatYearly = state.isRepeatYearly
                )
                reminderRepository.insert(reminder)

                _uiState.value = AddUiState(saveSuccess = true)
            } catch (e: Throwable) {
                Log.e("AddVM", "Failed to save reminder", e)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "保存失败: ${e.message}"
                )
            }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

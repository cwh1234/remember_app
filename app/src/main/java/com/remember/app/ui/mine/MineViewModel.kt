package com.remember.app.ui.mine

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.remember.app.RememberApp
import com.remember.app.data.database.entity.Holiday
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class MineUiState(
    val holidays: List<Holiday> = emptyList(),
    val isLoading: Boolean = true,
    val showAddHolidayDialog: Boolean = false,
    val newHolidayName: String = "",
    val newHolidayDate: Long = System.currentTimeMillis(),
    val errorMessage: String? = null
)

class MineViewModel(application: Application) : AndroidViewModel(application) {

    private val holidayRepository = try {
        (application as RememberApp).holidayRepository
    } catch (e: Throwable) {
        Log.e("MineVM", "Failed to init repository", e)
        throw e
    }

    private val _uiState = MutableStateFlow(MineUiState())
    val uiState: StateFlow<MineUiState> = _uiState.asStateFlow()

    init {
        loadHolidays()
    }

    private fun loadHolidays() {
        viewModelScope.launch {
            try {
                holidayRepository.getAllHolidays()
                    .catch { e ->
                        Log.e("MineVM", "Error loading holidays", e)
                        emit(emptyList())
                    }
                    .collect { holidays ->
                        _uiState.value = _uiState.value.copy(
                            holidays = holidays,
                            isLoading = false
                        )
                    }
            } catch (e: Throwable) {
                Log.e("MineVM", "Fatal error in loadHolidays", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "加载节假日失败: ${e.message}"
                )
            }
        }
    }

    fun toggleHolidayEnabled(id: Long, isEnabled: Boolean) {
        viewModelScope.launch {
            try {
                holidayRepository.setEnabled(id, isEnabled)
            } catch (e: Throwable) {
                Log.e("MineVM", "Failed to toggle holiday", e)
            }
        }
    }

    fun deleteHoliday(id: Long) {
        viewModelScope.launch {
            try {
                holidayRepository.deleteCustomHoliday(id)
            } catch (e: Throwable) {
                Log.e("MineVM", "Failed to delete holiday", e)
            }
        }
    }

    fun showAddDialog() {
        _uiState.value = _uiState.value.copy(
            showAddHolidayDialog = true,
            newHolidayName = "",
            newHolidayDate = System.currentTimeMillis()
        )
    }

    fun dismissAddDialog() {
        _uiState.value = _uiState.value.copy(showAddHolidayDialog = false)
    }

    fun updateNewHolidayName(name: String) {
        _uiState.value = _uiState.value.copy(newHolidayName = name)
    }

    fun updateNewHolidayDate(timestamp: Long) {
        _uiState.value = _uiState.value.copy(newHolidayDate = timestamp)
    }

    fun addCustomHoliday() {
        val state = _uiState.value
        if (state.newHolidayName.isBlank()) return

        viewModelScope.launch {
            try {
                val holiday = Holiday(
                    name = state.newHolidayName.trim(),
                    date = state.newHolidayDate,
                    isEnabled = true,
                    isDefault = false
                )
                holidayRepository.insert(holiday)
                _uiState.value = _uiState.value.copy(
                    showAddHolidayDialog = false,
                    newHolidayName = ""
                )
            } catch (e: Throwable) {
                Log.e("MineVM", "Failed to add holiday", e)
            }
        }
    }

    fun reEnableAllDefaults() {
        viewModelScope.launch {
            try {
                val defaults = _uiState.value.holidays.filter { it.isDefault && !it.isEnabled }
                defaults.forEach { holiday ->
                    holidayRepository.setEnabled(holiday.id, true)
                }
            } catch (e: Throwable) {
                Log.e("MineVM", "Failed to re-enable defaults", e)
            }
        }
    }
}

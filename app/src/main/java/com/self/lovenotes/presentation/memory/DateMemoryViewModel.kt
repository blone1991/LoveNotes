package com.self.lovenotes.presentation.memory

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.local.dao.PathDao
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.domain.usecase.DateMemoryUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class DateMemoryViewModel @Inject constructor(
    private val dateMemoryUsecase: DateMemoryUsecase,
    private val sharedPreferences: SharedPreferences,
    private val pathDao: PathDao,
) : ViewModel() {
    val users = dateMemoryUsecase.users.asStateFlow()
    val memories = dateMemoryUsecase.memories.asStateFlow()

    private val _isTracking =
        MutableStateFlow(sharedPreferences.getBoolean("TRACKING_SERVICE_RUNNING", false))
    val isTracking = _isTracking.asStateFlow()

    private val _selectedDate =
        MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _showEditMemoryDialog = MutableStateFlow<DateMemory?>(null)
    val showMemoryDialog = _showEditMemoryDialog.asStateFlow()

    init {
        viewModelScope.launch {
            dateMemoryUsecase.fetchUsers()
            fetchMemories()
        }
    }

    fun fetchMemories() {
        viewModelScope.launch {
            dateMemoryUsecase.fetchMemories(_selectedDate.value)
        }
    }

    fun selectDate(date: String) {
        val oldYearMonth = _selectedDate.value.substring(0, 7)
        val newYearMonth = date.substring(0, 7)
        _selectedDate.value = date
        if (oldYearMonth != newYearMonth) {
            fetchMemories()
        }
    }

    fun startTracking() {
        viewModelScope.launch {
            _isTracking.value = true
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            _isTracking.value = false

            val pathEntityList = pathDao.getPaths()

            _showEditMemoryDialog.value = DateMemory(
                uid = users.value.keys.toList()[0],
                geoList = pathEntityList.map { it.latlng },
            )
        }
    }


    fun openEditMemory(dateMemory: DateMemory) {
        _showEditMemoryDialog.value = dateMemory
    }

    fun closeEditMemeory() {
        _showEditMemoryDialog.value = null
    }


    fun updateMemory(dateMemory: DateMemory) {
        viewModelScope.launch {
            dateMemoryUsecase.updateMemory(dateMemory)

            fetchMemories()
        }
    }

    fun deleteMemory(dateMemory: DateMemory) {
        viewModelScope.launch {
            dateMemoryUsecase.deleteMemory(dateMemory)

            fetchMemories()
        }
    }
}
package com.self.lovenotes.presentation.memory

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.local.dao.PathDao
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.domain.usecase.DateMemoryUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class DateMemoryViewModel @Inject constructor(
    private val dateMemoryUsecase: DateMemoryUsecase,
    private val sharedPreferences: SharedPreferences,
    private val pathDao: PathDao,
) : ViewModel() {
    val users = dateMemoryUsecase.users
    val memories = dateMemoryUsecase.memories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isTracking =
        MutableStateFlow(sharedPreferences.getBoolean("TRACKING_SERVICE_RUNNING", false))
    val isTracking = _isTracking.asStateFlow()

    private val _selectedDate =
        MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _showEditMemoryDialog = MutableStateFlow<DateMemory?>(null)
    val showMemoryDialog = _showEditMemoryDialog.asStateFlow()


    fun startTracking() {
        viewModelScope.launch {
            pathDao.deletePaths()
            _isTracking.value = true
        }
    }

    fun stopTracking() {
        viewModelScope.launch {
            _isTracking.value = false

            val pathEntityList = pathDao.getPaths()

            _showEditMemoryDialog.value = DateMemory(
                uid = users.value.map { it.uid }[0],
                geoList = pathEntityList.map { it.latlng },
            )
        }
    }

    fun fetchDateMemoryMonth (yearMonth: YearMonth) {
        dateMemoryUsecase.onChangeMonth(yearMonth)
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
        }
    }

    fun deleteMemory(dateMemory: DateMemory) {
        viewModelScope.launch {
            dateMemoryUsecase.deleteMemory(dateMemory)
        }
    }
}
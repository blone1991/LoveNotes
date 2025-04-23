package com.self.lovenotes.presentation.memory

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.local.dao.PathDao
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.domain.usecase.DateMemoryUsecase
import com.self.lovenotes.service.TrackingService
import com.self.lovenotes.workers.startLocationTrackingWork
import com.self.lovenotes.workers.stopLocationTrackingWork
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class DateMemoryViewModel @Inject constructor(
    private val dateMemoryUsecase: DateMemoryUsecase,
    private val pathDao: PathDao,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    val users = dateMemoryUsecase.users
    val memories = dateMemoryUsecase.memories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _locationTrackingSession =
        MutableStateFlow(getSessionId())
    val locationTrackingSession = _locationTrackingSession.asStateFlow()

    private val _selectedDate =
        MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    val showEditMemoryDialog = MutableSharedFlow<DateMemory>()


    fun startTracking() {
        val sessionId = UUID.randomUUID().toString()
        context.getSharedPreferences("LoveNotes", Context.MODE_PRIVATE)
            .edit().putString("CURRENT_SESSION_ID", sessionId).apply()

        refreshHasSessionId();
        context.startForegroundService(Intent(context, TrackingService::class.java))

//        startLocationTrackingWork(context)
    }

    fun stopTracking() {
//        stopLocationTrackingWork(context)

        viewModelScope.launch {
            val sessionId = context.getSharedPreferences("LoveNotes", Context.MODE_PRIVATE)
                .getString("CURRENT_SESSION_ID", null)

            sessionId?.let {
                val paths = pathDao.getPathsBySession(it)

                if (paths.isNotEmpty()) {
                    updateMemory(
                        DateMemory(
                            date = paths[0].date,
                            uid = users.value.map { it.uid }[0],
                            geoList = paths.map { it.latlng },
                        )
                    )
                }

                discardTrackingSession()
            }
        }
    }

    fun discardTrackingSession() {
        viewModelScope.launch {
            locationTrackingSession.value ?.let{
                pathDao.deleteBySession(it)
            }
            clearSessionId()
        }
    }

    private fun clearSessionId() = viewModelScope.launch {
        context.getSharedPreferences("LoveNotes", Context.MODE_PRIVATE)
            .edit().remove("CURRENT_SESSION_ID").apply()

        refreshHasSessionId();
    }

    private fun getSessionId(): String? {
        return context.getSharedPreferences("LoveNotes", Context.MODE_PRIVATE)
            .getString("CURRENT_SESSION_ID", null)
    }

    private fun refreshHasSessionId() {
        _locationTrackingSession.value = getSessionId();
    }

    fun fetchDateMemoryMonth (yearMonth: YearMonth) {
        dateMemoryUsecase.onChangeMonth(yearMonth)
    }


    fun openEditMemory(dateMemory: DateMemory) {
        viewModelScope.launch {
            showEditMemoryDialog.emit(dateMemory)
        }
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
package com.self.lovenotes.ui.Calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.model.Event
import com.self.lovenotes.data.repository.EventRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val eventRepository: EventRepository,
) : ViewModel() {
    private val _selectedDate = MutableStateFlow(getCurrentDateString())
    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events.asStateFlow()

    private val _popupDialog = MutableStateFlow(false)
    val popupDialog = _popupDialog.asStateFlow()

    init {
        viewModelScope.launch {
            init()
        }
    }

    private suspend fun init() {
        _events.value = eventRepository.fetchEvents(_selectedDate.value)
    }

    fun showDialog(display: Boolean) {
        _popupDialog.value = display
    }

    fun selectDate(date: String) {
        _selectedDate.value = date

        viewModelScope.launch {
            _events.value = eventRepository.fetchEvents(_selectedDate.value)
        }

    }

    fun addEvent(title: String) {
        viewModelScope.launch {
            eventRepository.addEvent(title, _selectedDate.value)

            _events.value = eventRepository.fetchEvents(_selectedDate.value)
        }
    }

    private fun getCurrentDateString(): String {
        val current = LocalDate.now()
        return String.format("%d-%02d-%02d", current.year, current.monthValue, current.dayOfMonth)
    }

    fun formatDate(date: String): String {
        val parts = date.split("-")
        if (parts.size == 3) {
            val year = parts[0].toInt()
            val month = parts[1].toInt()
            val day = parts[2].toInt()
            return "${getMonthName(month)} $day, $year"
        }
        return date
    }

    private fun getMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> "Unknown"
        }
    }
}
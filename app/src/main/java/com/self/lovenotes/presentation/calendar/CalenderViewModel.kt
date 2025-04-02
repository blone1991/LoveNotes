package com.self.lovenotes.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.remote.model.Event
import com.self.lovenotes.domain.usecase.CalendarUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject


@ExperimentalCoroutinesApi
@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val calendarUsecase: CalendarUsecase,
) : ViewModel() {
//    private val _selectedDate = MutableStateFlow(LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE))
//    val selectedDate: StateFlow<String> = _selectedDate.asStateFlow()

    private val _showEditEventDialog = MutableStateFlow<Event?>(null)
    val showEventDialog = _showEditEventDialog.asStateFlow()
    val selectedMonth = calendarUsecase.selectedMonth
    val users = calendarUsecase.users
    val events = calendarUsecase.events.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    fun showEditEventDialog(event: Event?) {
        _showEditEventDialog.value = event
    }

    fun submitEvent(event: Event) {
        viewModelScope.launch {
            calendarUsecase.updateEvent(event)
        }
    }

    fun deleteEvent(event: Event) {
        viewModelScope.launch {
            calendarUsecase.deleteEvent(event)
        }
    }

    fun onChangeMonth (yearMonth: YearMonth) {
        calendarUsecase.onChangeMonth(yearMonth)
    }

    fun formatDate(date: LocalDate): String {
        val year = date.year
        val month = date.monthValue
        val day = date.dayOfMonth
        return "${getMonthName(month)} $day, $year"
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
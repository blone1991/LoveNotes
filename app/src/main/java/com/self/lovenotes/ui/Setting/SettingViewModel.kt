package com.self.lovenotes.ui.Setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.domain.CalendarUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val calendarUsecase: CalendarUsecase,
) : ViewModel() {
    val users = calendarUsecase.users.asStateFlow()

    init {
        viewModelScope.launch {
            calendarUsecase.fetchUsers()
        }
    }

    fun subscribe(code: String) {
        viewModelScope.launch {
            calendarUsecase.subscribe(code)
        }
    }

    fun clearSubscribe() {
        viewModelScope.launch {
            calendarUsecase.clearSubscribe()
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            calendarUsecase.updateNickname(nickname)
        }
    }
}
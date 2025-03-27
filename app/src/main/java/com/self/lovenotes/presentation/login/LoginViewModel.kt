package com.self.lovenotes.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.domain.usecase.CalendarUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val calendarUsecase: CalendarUsecase,
) : ViewModel() {
    val user = calendarUsecase.users.asStateFlow()

    private val _permissionReady = MutableStateFlow(false)
    val permissionReady = _permissionReady.asStateFlow()

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch { calendarUsecase.fetchUsers() }
    }
}
package com.self.lovenotes.presentation.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.domain.usecase.DateMemoryUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class DateMemoryViewModel @Inject constructor(
    private val dateMemoryUsecase: DateMemoryUsecase,
) : ViewModel() {
    val users = dateMemoryUsecase.users.asStateFlow()
    val memories = dateMemoryUsecase.memories.asStateFlow()

    private val _isTracking = MutableStateFlow(false)
    val isTracking = _isTracking.asStateFlow()

    init {
        viewModelScope.launch {
            dateMemoryUsecase.fetchUsers()
            dateMemoryUsecase.fetchMemories()
        }
    }

    fun fetchMemories() {
        viewModelScope.launch {
            dateMemoryUsecase.fetchMemories()
        }
    }


    fun startTracking() {
        _isTracking.value = true
    }

    fun stopTracking() {
        _isTracking.value = false
    }
}
package com.self.lovenotes.ui.Setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.model.User
import com.self.lovenotes.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _myInviteCode = MutableStateFlow(User("", ""))
    val myInviteCode = _myInviteCode.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            loadUser()
        }
    }

    private suspend fun loadUser() {
        userRepository.getUser()?.let { _myInviteCode.value = it }
    }

    fun addSubscribe(code: String) {
        viewModelScope.launch {
            userRepository.addSubscribing(code)

            loadUser()
        }
    }

    fun clearSubscrible() {
        viewModelScope.launch {
            userRepository.clearSubscribing()

            loadUser()
        }
    }
}
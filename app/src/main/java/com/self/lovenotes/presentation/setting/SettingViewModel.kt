package com.self.lovenotes.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.data.remote.repository.UserRepository
import com.self.lovenotes.domain.usecase.CalendarUsecase
import com.self.lovenotes.domain.usecase.SettingUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingUsecase: SettingUsecase
) : ViewModel() {
    val users = settingUsecase.users

    fun subscribe(code: String) {
        viewModelScope.launch {
            settingUsecase.subscribe(code)
        }
    }

    fun deleteSubscribe(user: User) {
        viewModelScope.launch {
            settingUsecase.deleteSubscribe(user)
        }
    }

    fun updateNickname(nickname: String) {
        viewModelScope.launch {
            settingUsecase.updateNickname(nickname)
        }
    }
}
package com.self.lovenotes.domain.usecase

import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.data.remote.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SettingUsecase @Inject constructor(
    private val userRepository: UserRepository
){
    val users = userRepository.userInfos

    suspend fun subscribe(code: String) = withContext(Dispatchers.IO) {
        userRepository.findUserByInvitationCode(code)?.let { invitor ->
            userRepository.getUser()?.let {
                userRepository.updateUser(it.copy(subscribing = it.subscribing + invitor.uid))
            }
        }
    }

    suspend fun deleteSubscribe(user: User) = withContext(Dispatchers.IO) {
        userRepository.getUser()?.let {
            userRepository.updateUser(it.copy(subscribing = it.subscribing.filter { it != user.uid }))
        }
    }

    suspend fun updateNickname(nickname: String) = withContext(Dispatchers.IO) {
        userRepository.getUser()?.let {
            userRepository.updateUser(it.copy(nickname = nickname))
        }
    }
}
package com.self.lovenotes.domain.usecase

import android.util.Log
import com.self.lovenotes.data.remote.model.Event
import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.data.remote.repository.EventRepository
import com.self.lovenotes.data.remote.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarUsecase @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
) {
    val users = MutableStateFlow<Map<String, User>>(emptyMap()) // [0] : 내 정보 [1 ~ ] 내가 구독하는 User 정보
    val events = MutableStateFlow<List<Event>>(emptyList())     // 월 단위 이벤트

    suspend fun loginWithGoogle( ) {
        userRepository.login()
    }

    suspend fun fetchUsers() = withContext(Dispatchers.IO) {
        val map: MutableMap<String, User> = mutableMapOf()

        val myUid = userRepository.login() ?: return@withContext
        val my = userRepository.getUser(myUid) ?: return@withContext

        map[my.uid] = my
        my.subscribing
            .mapNotNull { userRepository.getUser(it) }
            .forEach { map.put(it.uid, it) }

        users.value = map
    }

    // 월 단위 이벤트 조회
    suspend fun fetchEvents(date: String) = withContext(Dispatchers.IO) {
        try {
            val yearMonth = date.split("-").let { "${it[0]}-${it[1]}" }
            events.value = users.value.keys
                .map { eventRepository.getEventsMontly(it, yearMonth) }
                .reduce { acc, events -> acc + events }
                .toList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("CalendarUsecase", "월 이벤트 정보 조회 실패")
            events.value = emptyList()
        }
    }

    suspend fun updateEvent(event: Event) = withContext(Dispatchers.IO) {
        eventRepository.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event) = withContext(Dispatchers.IO) {
        eventRepository.deleteEvent(event)
    }

    suspend fun subscribe(code: String) = withContext(Dispatchers.IO) {
        userRepository.findUserByInvitationCode(code)?.let { invitor ->
            userRepository.getUser()?.let {
                userRepository.updateUser(it.copy(subscribing = it.subscribing + invitor.uid))
            }
        }

        fetchUsers()
    }

    suspend fun deleteSubscribe(user: User) = withContext(Dispatchers.IO) {
        userRepository.getUser()?.let {
            userRepository.updateUser(it.copy(subscribing = it.subscribing.filter { it != user.uid }))
        }

        fetchUsers()
    }

    suspend fun updateNickname(nickname: String) = withContext(Dispatchers.IO) {
        userRepository.getUser()?.let {
            userRepository.updateUser(it.copy(nickname = nickname))
        }

        fetchUsers()
    }
}
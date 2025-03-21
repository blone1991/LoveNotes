package com.self.lovenotes.domain

import android.util.Log
import com.self.lovenotes.data.model.Event
import com.self.lovenotes.data.model.User
import com.self.lovenotes.data.repository.EventRepository
import com.self.lovenotes.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CalendarUsecase @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
) {
    val users = MutableStateFlow<Map<String, User>>(emptyMap())
    val events = MutableStateFlow<List<Event>>(emptyList())

    suspend fun fetchUsers() = withContext(Dispatchers.IO) {
        val map: MutableMap<String, User> = mutableMapOf()

        val myUid = userRepository.login()?: return@withContext
        val my = userRepository.getUser(myUid) ?: return@withContext

        map[my.uid] = my
        my.subscribing
            .mapNotNull { userRepository.getUser(it) }
            .forEach { map.put(it.uid, it) }

        users.value = map
    }

    suspend fun fetchEvents(date: String) = withContext(Dispatchers.IO) {
        try {
            events.value = users.value.keys
                .map { eventRepository.getEventsForDate(it, date) }
                .reduce { acc, events -> acc + events }
                .toList()

        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EventRepository", "이벤트 정보 조회 실패")
            emptyList<Event>()
        }
    }

    suspend fun updateEvent(event: Event) = withContext(Dispatchers.IO) {
        eventRepository.updateEvent(event)
    }

    suspend fun subscribe (code: String) = withContext(Dispatchers.IO) {
        userRepository.addSubscribing(code)
        fetchUsers()
    }

    suspend fun clearSubscribe () = withContext(Dispatchers.IO) {
        userRepository.clearSubscribing()
        fetchUsers()
    }

    suspend fun updateNickname (nickname: String) = withContext(Dispatchers.IO) {
        userRepository.updateNickname(nickname)
        fetchUsers()
    }
}
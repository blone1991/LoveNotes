package com.self.lovenotes.domain.usecase

import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.data.remote.repository.DateMemoryRepository
import com.self.lovenotes.data.remote.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DateMemoryUsecase @Inject constructor(
    private val userRepository: UserRepository,
    private val dateMemoryRepository: DateMemoryRepository,
) {
    val users = MutableStateFlow<Map<String, User>>(emptyMap()) // [0] : 내 정보 [1 ~ ] 내가 구독하는 User 정보
    val memories = MutableStateFlow<List<DateMemory>>(emptyList())

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

    suspend fun fetchMemories() = withContext(Dispatchers.IO) {
        val myUid = userRepository.login() ?: return@withContext

        val myMemories = dateMemoryRepository.getDateMemeoriesForUid(myUid)
        val sharedMemories = dateMemoryRepository.getDateMemeoriesForShareWith(myUid)

        memories.value = myMemories + sharedMemories
    }

    suspend fun deleteMemoriy(memory: DateMemory) = withContext(Dispatchers.IO) {
        dateMemoryRepository.deleteDateMemory(memory)
    }
}
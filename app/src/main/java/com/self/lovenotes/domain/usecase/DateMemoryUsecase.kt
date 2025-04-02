package com.self.lovenotes.domain.usecase

import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.data.remote.repository.DateMemoryRepository
import com.self.lovenotes.data.remote.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import java.time.YearMonth
import javax.inject.Inject

@ExperimentalCoroutinesApi
class DateMemoryUsecase @Inject constructor(
    private val userRepository: UserRepository,
    private val dateMemoryRepository: DateMemoryRepository,
) {
    val users = userRepository.userInfos

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth = _selectedMonth.asStateFlow()

    val memoryCache = mutableMapOf<String, List<DateMemory>>()

    val memories: Flow<List<DateMemory>> = combine(
        selectedMonth,
        users // 내 UID 가져오기
    ) { yearMonth, currentUser ->
        Pair(yearMonth, users.value.map { it.uid }.first())
    }.flatMapLatest { (yearMonth, myUid) ->
        val cacheFlow = flowOf(memoryCache[yearMonth.toString()] ?: emptyList())
        val serverFlow = combine(
            dateMemoryRepository.getMyMemoriesFlow(myUid, yearMonth),
            dateMemoryRepository.getSharedMemoriesFlow(myUid, yearMonth)
        ) { shared, mine ->
            (shared + mine).distinctBy { it.id } // 병합 후 중복 제거
        }
        merge(cacheFlow, serverFlow)
    }.onEach {
        memoryCache[selectedMonth.value.toString()] = it
    }.flowOn(Dispatchers.IO)

    fun onChangeMonth (yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }

    suspend fun updateMemory(memory: DateMemory) = withContext(Dispatchers.IO) {
        dateMemoryRepository.updateDateMemory(memory)
    }

    suspend fun deleteMemory(memory: DateMemory) = withContext(Dispatchers.IO) {
        dateMemoryRepository.deleteDateMemory(memory)
    }
}
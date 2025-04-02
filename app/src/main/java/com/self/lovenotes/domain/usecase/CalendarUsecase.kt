package com.self.lovenotes.domain.usecase

import androidx.compose.runtime.collectAsState
import com.self.lovenotes.data.remote.model.Event
import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.data.remote.repository.EventRepository
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
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext
import okhttp3.internal.format
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@ExperimentalCoroutinesApi
class CalendarUsecase @Inject constructor(
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
) {
    val users = userRepository.userInfos
    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth = _selectedMonth.asStateFlow()

    val events: Flow<List<Event>> = combine(users, selectedMonth) { userList, yearMonth ->
        Pair(userList.map { it.uid }, yearMonth)
    }.flatMapLatest { (userIds, yearMonth) ->
        if (userIds.isEmpty()) {
            flowOf(emptyList()) // 타입 명시
        } else {
            eventRepository.getEventsMontlyFlow(userIds, yearMonth)
        }
    }.flowOn(Dispatchers.IO)


    fun onChangeMonth (yearMonth: YearMonth) {
        _selectedMonth.value = yearMonth
    }

    suspend fun updateEvent(event: Event) = withContext(Dispatchers.IO) {
        eventRepository.updateEvent(event)
    }

    suspend fun deleteEvent(event: Event) = withContext(Dispatchers.IO) {
        eventRepository.deleteEvent(event)
    }


}
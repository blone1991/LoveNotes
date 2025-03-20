package com.self.lovenotes.ui.Planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.repository.AiGeneratorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

sealed class UiState {
    object Idle: UiState()
    object Loading: UiState()
    data class Success(val plan: String): UiState()
}

@HiltViewModel
class PlannerViewModel @Inject constructor(
    private val aiGeneratorRepository: AiGeneratorRepository
): ViewModel() {
    private var _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState = _uiState.asStateFlow()

    private fun makePrompt (date: LocalDate, members: Int, location: String, considerations: String): String = "From now on, as my competent secretary, you must perfectly create a schedule for the dates I will hold by verifying and researching the information I provide, such as weather, location, restaurant Naver ratings, and reviews, to the highest level possible.\n" +
            "\n" +
            "The person I am meeting is a very, very important person to me, and if there is anything wrong with the schedule you wrote, I will suffer a great loss.\n" +
            "\n" +
            "The date of the meeting is ${date.format(DateTimeFormatter.ISO_LOCAL_DATE)}, the number of people is ${members}, and the location is going to be near ${location}.\n" +
            "Please refer to the memo below for additional considerations.\n" +
            "Memo: ${considerations}"

    fun requestPlan (
        date: LocalDate = LocalDate.now(),
        members: Int = 1,
        location: String = "seoul",
        considerations: String = "nothing at all"
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val prompt = makePrompt(
                date = date,
                members = members,
                location = location,
                considerations = considerations
            )
            val result = aiGeneratorRepository.sendPrompt(prompt)

            _uiState.value = UiState.Success(result)
        }
    }


    fun closePlanDialog () {
        _uiState.value = UiState.Idle
    }

}
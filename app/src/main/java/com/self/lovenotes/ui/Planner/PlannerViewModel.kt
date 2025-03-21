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

    private fun makePrompt (date: LocalDate, members: Int, location: String, considerations: String): String =
        """
            앞으로 너는 유능한 비서로서 내가 제공하는 정보인 날짜, 회원 수, 대략적인 위치, 그리고 메모를 통해 최고의 일정을 결정해 줘.
            
            네가 고려해야 할 내용들은 기상청 정보를 이용한 해당 날짜의 날씨, 장소 주변의 교통상황과 동선을 고려해야하고, 
            맛집은 네이버 별점 기준으로 찾아보고, 제공된 지역정보 주변에 열리는 이벤트나 팝업스토어 정보를 고려해줘

            메모내요이 충분히 제공되지 않는다면 적당히 네가 정해주고,
            결과로는 아래와 같은 형식으로 제공해 줘
            
            목적: {}
            테마: {}
            전체 계획 { 예를들어
            13:00 ~ 14:00 : 점심식사
             - 후보 1 : (레스토랑 이름) 
                - 추천 이유 
                - 별점정보 또는 예산
             - 후보 2 : (레스토랑 이름)
                - 추천 이유 
                - 별점정보 또는 예산   
             - 후보 3 : (레스토랑 이름)
                - 추천 이유 
                - 별점정보 또는 예산
             
            14:00 ~ 15:00 : 액티비티
             - 후보 1 : (액티비티 설명) - 추천 이유 - 링크 또는 예산
             - 후보 2 : (액티비티 설명) - 추천 이유 - 링크 또는 예산
             - 후보 3 : (액티비티 설명) - 추천 이유 - 링크 또는 예산
            14:00 ~ 15:00 : 액티비티
            ...
            }
            
            이런식으로 작성해 줘

            그리고 나는 추가로 검증을 할 시간이 없어. 위의 자료조사를 통해 최적의 방안도 표시해 줘
            내가 만나는 사람은 나에게 아주아주 중요한 사람이고, 일정에 문제가 큰 손실이 생길 수도 있어.

            그럼 아래 정보를 제공할게.

            날짜: ${date}
            장소: ${location}
            회원 수: ${members}
            메모: ${considerations}
        """.trimIndent()

    fun requestPlan (
        date: LocalDate = LocalDate.now(),
        members: Int = 2,
        location: String = "강남역",
        considerations: String = "Noting At All"
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
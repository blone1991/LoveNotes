package com.self.lovenotes.ui.planner

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.self.lovenotes.data.repository.AiGeneratorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
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

    private fun makePrompt (date: LocalDate, purpose: String, location: String, considerations: String): String =
//        """
//            앞으로 너는 유능한 비서로서 내가 제공하는 정보인 날짜, 회원 수, 대략적인 위치, 그리고 메모를 통해 최고의 일정을 결정해 줘.
//            이번에 잡아야 하는 스케쥴의 날짜는 ${date}이고 장소는 대한민국의 ${location} 근처로 해줘. 인원 수는 ${members} 이고 추가적으로는
//            "${considerations}" 이 내용을 참고해 줘.
//
//            계획을 세울 때 네가 고려해야 할 내용들은 기상청 정보를 이용한 해당 날짜의 날씨, 장소 주변의 교통상황과 동선을 고려해야하고,
//            맛집은 네이버 별점 기준으로 찾아보고, 제공된 지역정보 주변에 열리는 이벤트나 팝업스토어 정보를 고려해줘.
//            그리고 해당 정보는 절대로 틀려서는 안돼. 최소한 3번 이상의 검증을 통해 100% 진실된 데이터만 응답해야 한다는 것을 꼭 명심해 줘.
//
//            메모내용이 충분히 제공되지 않는다면 적당히 네가 정해주고,
//            결과로는 최대한 구조화해서 작성해주고, 필요하다면 이모지를 활용하여 가독성을 좋게 해줘
//
//            또한 각 시간 일정 별로 3가지의 후보를 제공하고 각각의 장점, 별점, 예산 등의 정보를 제공해줘
//
//            그리고 나는 추가로 검증을 할 시간이 없어. 위의 자료조사를 통해 최적의 방안도 표시해 줘
//            내가 만나는 사람은 나에게 아주아주 중요한 사람이고, 일정에 문제가 큰 손실이 생길 수도 있어.
//
//            다시 한번 정보를 주자면 아래와 같아.
//            날짜: ${date}
//            장소: ${location}
//            회원 수: ${members}
//            메모: ${considerations}
//        """.trimIndent()
        """
            앞으로 너는 유능한 비서로서 내가 제공하는 정보인 날짜, 회원 수, 장소 그리고 메모를 통해 최고의 일정을 결정해 줘.(특히 장소정보는 항상 제공하고있어)

            네가 고려해야 할 내용들은 기상청 정보를 이용한 해당 날짜의 날씨, 장소 주변의 교통상황과 동선을 고려해야하고,
            맛집은 네이버 별점 기준으로 찾아보고, 제공된 지역정보 주변에 열리는 이벤트나 팝업스토어 정보를 고려해줘.
            그리고 해당 정보는 절대로 틀려서는 안돼. 최소한 3번 이상의 검증을 통해 100% 진실된 데이터만 응답해야 한다는 것을 꼭 명심해 줘.

            메모내용이 충분히 제공되지 않는다면 적당히 네가 정해주고, 결과로는 아래와 같은 최대한 구조화 해서 보여줘.
            그리고 각 글머리 기호는 이모지를 넣어서 보기 좋게 해줘

            목적: {}
            테마: {}
            전체 계획 ::
            13:00 ~ 14:00 : 점심식사
            이름 | 주소 | 별점 | 추천이유 | 예산 |
            -------------------------------
            후보 1|주소1 | 별점1 | 추천이유1| 예산1|
            -------------------------------
            후보 2|주소2 | 별점2 | 추천이유2| 예산2|
            -------------------------------
            후보 3|주소3 | 별점3 | 추천이유3| 예산3|
            -------------------------------
            

            이런 형식 비슷하게 최대한 구조화 해서 작성해 줘.
            
            날짜: ${date} 
            장소: ${location}
            목적: ${purpose}
            메모: ${considerations}

            그리고 나는 추가로 검증을 할 시간이 없어. 위의 자료조사를 통해 최적의 방안도 표시해 줘
            내가 만나는 사람은 나에게 아주아주 중요한 사람이고, 일정에 문제가 큰 손실이 생길 수도 있어.
            
        """.trimIndent()

    fun requestPlan (
        date: LocalDate = LocalDate.now(),
        purpose: String,
        location: String = "서울 관악구 봉천동 1604-8",
        considerations: String = ""
    ) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            val prompt = makePrompt(
                date = date,
                purpose = purpose,
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
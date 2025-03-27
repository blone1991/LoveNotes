import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*
//import androidx.compose.foundation.pager.HorizontalPager
//import androidx.compose.foundation.pager.rememberPagerState
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BasicPagerCalendar(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit = {},
) {
    val today = LocalDate.now()
    val initialPage = 6
    val pageCount = 18
    val pagerState = rememberPagerState(initialPage = initialPage) // 초기 페이지 설정
    val currentMonth = remember { mutableStateOf(YearMonth.from(today)) }
    val coroutineScope = rememberCoroutineScope()

    // 페이지가 변경될 때마다 currentMonth 업데이트
    LaunchedEffect(pagerState.currentPage) {
        currentMonth.value =
            YearMonth.from(today).plusMonths((pagerState.currentPage - initialPage).toLong())
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // 캘린더 헤더 (이전/다음 달 이동 버튼, 현재 월 표시)
        CalendarHeader(
            currentMonth = currentMonth.value,
            onPreviousMonthClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                }
            },
            onNextMonthClick = {
                coroutineScope.launch {
                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                }
            }
        )
        // 요일 표시
        DaysOfWeek()

        Spacer(modifier = Modifier.height(5.dp))

        HorizontalPager(
            // 패키지 명시적 지정
            count = pageCount,
            state = pagerState,
        ) { page ->
            val month = YearMonth.from(today)
                .plusMonths((page - initialPage).toLong()) // 해당 페이지의 YearMonth 계산

            CalendarDays(
                currentMonth = month,
                selectedDate = selectedDate, // 선택된 날짜 로직 추가 필요
                onDateSelected = onDateSelected
            )
        }
    }
}

@Composable
fun CalendarHeader(
    currentMonth: YearMonth,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPreviousMonthClick) {
//            Text("<")
            Icon(Icons.Default.KeyboardArrowLeft, "")
        }
        Text(
            text = currentMonth.format(
                DateTimeFormatter.ofPattern(
                    "MMMM yyyy",
                    Locale.getDefault()
                )
            ),
            style = MaterialTheme.typography.titleMedium
        )

        IconButton(onClick = onNextMonthClick) {
            Icon(Icons.Default.KeyboardArrowRight, "")
        }
    }
}

@Composable
fun DaysOfWeek() {
    Row(modifier = Modifier.fillMaxWidth()) {
        val daysOfWeek = DayOfWeek.values()
        for (dayOfWeek in daysOfWeek) {
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun CalendarDays(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
) {
    val firstDayOfMonth = currentMonth.atDay(1)
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek
    val daysInMonth = currentMonth.lengthOfMonth()

    val firstRowStart =
        firstDayOfMonth.minusDays(firstDayOfWeek.value.toLong() % 7) // 첫 번째 행 시작 날짜 계산

    Column {
        for (week in 0..5) { // 최대 6주까지 표시
            Row(modifier = Modifier.fillMaxWidth()) {
                for (day in 0..6) {
                    val date = firstRowStart.plusDays((week * 7 + day).toLong())
                    if (date.yearMonth == currentMonth) {
                        DateView(
                            modifier = Modifier.weight(1f),
                            date = date,
                            isSelected = date == selectedDate, // selectedDate 로직 수정 필요
                            onDateSelected = onDateSelected
                        )
                    } else {
                        // 현재 달에 속하지 않는 날짜는 빈 공간으로 처리
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun DateView(
    modifier: Modifier,
    date: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit,
) {
    IconButton(
        enabled = !isSelected,
        onClick = { onDateSelected(date) },
        modifier = modifier,
        colors = IconButtonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = Color.Black,
        )

    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontSize = 16.sp,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.Black
        )
    }
}

// 확장 함수 (YearMonth 비교를 더 쉽게 하기 위해)
private val LocalDate.yearMonth: YearMonth
    get() = YearMonth.from(this)
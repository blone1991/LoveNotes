package com.self.lovenotes.ui.Calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CalendarView(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 12 }) // 12개월
    val selectedLocalDate = remember(selectedDate) {
        LocalDate.parse(selectedDate, DateTimeFormatter.ISO_LOCAL_DATE)
    }

    val currentYear = LocalDate.now().year
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue - 1) }

    LaunchedEffect(selectedLocalDate) {
        currentMonth = selectedLocalDate.monthValue - 1
        pagerState.animateScrollToPage(currentMonth)
    }

    Column {
        // 월 표시 및 네비게이션
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth -= 1 }) {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month"
                )
            }
            Text(
                text = "${getMonthName(currentMonth)} $currentYear",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { currentMonth += 1 }) {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "Next Month"
                )
            }
        }

//        HorizontalPager(state = pagerState) { page ->
//            currentMonth = page + 1
        MonthCalendar(
            year = currentYear,
            month = currentMonth,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
//        }
    }
}

@Composable
fun MonthCalendar(
    year: Int,
    month: Int,
    selectedDate: String,
    onDateSelected: (String) -> Unit,
) {
    val daysInMonth = getDaysInMonth(year, month)
    val firstDayOfWeek = LocalDate.of(year, month, 1).dayOfWeek.value % 7 // 0(월) ~ 6(일)

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = "Previous Month"
                )
            }
            Text(
                text = "${getMonthName(month)} $year",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowRight,
                    contentDescription = "Next Month"
                )
            }
        }

        // 요일 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }

        // 날짜 그리드
        var dayCounter = 1
        for (week in 0 until 6) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0 until 7) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                        Text(
                            text = "",
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                    } else {
                        val dateStr = String.format("%d-%02d-%02d", year, month, dayCounter)
                        val isSelected = dateStr == selectedDate
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .padding(4.dp)
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { onDateSelected(dateStr) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}

// 월별 일수 계산 (외부 의존성 없이)
fun getDaysInMonth(year: Int, month: Int): Int {
    return when (month) {
        2 -> if (isLeapYear(year)) 29 else 28
        4, 6, 9, 11 -> 30
        else -> 31
    }
}

fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

fun getMonthName(month: Int): String {
    return when (month) {
        1 -> "January"
        2 -> "February"
        3 -> "March"
        4 -> "April"
        5 -> "May"
        6 -> "June"
        7 -> "July"
        8 -> "August"
        9 -> "September"
        10 -> "October"
        11 -> "November"
        12 -> "December"
        else -> "Unknown"
    }
}
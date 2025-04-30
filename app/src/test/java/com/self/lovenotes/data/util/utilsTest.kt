package com.self.lovenotes.data.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class utilsTest {

    @ParameterizedTest
    @CsvSource(
        "2025-01-09, 2025-01-01, 2025-01-31",
        "2025-02-09, 2025-02-01, 2025-02-28",
        "2025-03-09, 2025-03-01, 2025-03-31",
        "2025-04-09, 2025-04-01, 2025-04-30",
        "2025-05-09, 2025-05-01, 2025-05-31",
        "2025-06-09, 2025-06-01, 2025-06-30",
        "2025-07-09, 2025-07-01, 2025-07-31",
        "2025-08-09, 2025-08-01, 2025-08-31",
        "2025-09-09, 2025-09-01, 2025-09-30",
        "2025-10-09, 2025-10-01, 2025-10-31",
        "2025-11-09, 2025-11-01, 2025-11-30",
        "2025-12-09, 2025-12-01, 2025-12-31",
    )
    fun `2025년 1~12월 입력 시 각 월의 시작일과 종료일이 반환되어야한다` (input: String, expected_start: String, expected_end: String) {
        // Action
        val (start, end) = utils.getMonthRange(YearMonth.parse(input, DateTimeFormatter.ISO_LOCAL_DATE))
        println("getMonthRange ${input} -> start = ${start}, end = ${end}")
        // Assert
        assertEquals(expected_start, start)
        assertEquals(expected_end, end)
    }

}
package com.self.lovenotes.presentation.calendar

import com.google.firebase.perf.trace
import com.self.lovenotes.data.remote.model.Event
import com.self.lovenotes.domain.usecase.CalendarUsecase
import io.mockk.Awaits
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.YearMonth

@ExtendWith(MockKExtension::class)
class CalendarViewModelTest {

    lateinit var usecase: CalendarUsecase
    lateinit var viewModel: CalendarViewModel

    private val testDispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup () {
        Dispatchers.setMain(testDispatcher)

        usecase = mockk<CalendarUsecase>(relaxed = true)
        viewModel = CalendarViewModel(usecase)
    }

    @AfterEach
    fun tearDown () {
        Dispatchers.resetMain()
    }

    @Test
    fun submitEvent() = runTest {
        val dummyEvent = Event(title = "test", date = "2025-04-30")

        coEvery { usecase.updateEvent(any()) } returns Unit

        viewModel.submitEvent(dummyEvent)

        // launch 블록 안 코드 실행
        advanceUntilIdle()

        coVerify { usecase.updateEvent(any()) }
    }

    @Test
    fun deleteEvent() = runTest {
        val dummyEvent = Event(title = "test", date = "2025-04-30")

        coEvery { usecase.deleteEvent(any()) } returns Unit

        viewModel.deleteEvent(dummyEvent)

        // launch 블록 안 코드 실행
        advanceUntilIdle()

        coVerify { usecase.deleteEvent(any()) }
    }

    @Test
    fun onChangeMonth() = runTest {
        val dummyEvent = Event(title = "test", date = "2025-04-30")

        every { usecase.onChangeMonth(any()) } returns Unit

        viewModel.onChangeMonth(YearMonth.now())

        // launch 블록 안 코드 실행

        verify { usecase.onChangeMonth(any()) }
    }
}
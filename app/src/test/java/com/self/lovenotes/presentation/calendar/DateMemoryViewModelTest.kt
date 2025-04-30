package com.self.lovenotes.presentation.calendar

import android.content.Context
import android.content.SharedPreferences
import com.self.lovenotes.data.local.dao.PathDao
import com.self.lovenotes.data.local.entity.PathEntity
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.domain.usecase.DateMemoryUsecase
import com.self.lovenotes.presentation.memory.DateMemoryViewModel
import io.mockk.Ordering
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import net.bytebuddy.matcher.ModifierMatcher
import org.junit.jupiter.api.Assertions.*

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DateMemoryViewModelTest {

    val usecase = mockk<DateMemoryUsecase>(relaxed = true)
    lateinit var viewModel: DateMemoryViewModel
    val pathDao = mockk<PathDao>()
    val context = mockk<Context>(relaxed = true)
    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = DateMemoryViewModel(dateMemoryUsecase = usecase, pathDao= pathDao, context)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun updateMemory() = runTest {

        coEvery { usecase.updateMemory(any()) } just Runs

        viewModel.updateMemory(DateMemory(geoList= emptyList()))

        advanceUntilIdle()

        coVerify { usecase.updateMemory(any()) }

    }

    @Test
    fun `메모리를 지운다 usecase 호출`() = runTest{
        coEvery { usecase.deleteMemory(any()) } just Runs

        viewModel.deleteMemory(DateMemory(geoList= emptyList()))

        advanceUntilIdle()

        coVerify { usecase.deleteMemory(any()) }
    }

    @Test
    fun `위치기록을 시작한다`() {
        val sharedPreferences = mockk<SharedPreferences>(relaxed = true)
        val sharedPreferencesEditor = mockk<SharedPreferences.Editor>(relaxed = true)

        every { context.getSharedPreferences(any(), eq(Context.MODE_PRIVATE)) } returns sharedPreferences
        every { sharedPreferences.getString(eq("CURRENT_SESSION_ID"), any()) } returns "test_session_id"
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.putString(any(), any()) } returns sharedPreferencesEditor
        every { sharedPreferencesEditor.apply() } just Runs
        every { context.startForegroundService(any()) } returns null

        viewModel.startTracking()


        verify{
            sharedPreferences.edit()
            context.startForegroundService(any())
        }
        assertEquals("test_session_id", sharedPreferences.getString("CURRENT_SESSION_ID", null))
    }
}
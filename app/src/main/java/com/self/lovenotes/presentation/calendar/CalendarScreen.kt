package com.self.lovenotes.presentation.calendar

import BasicPagerCalendar
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.self.lovenotes.data.remote.model.Event
import com.self.lovenotes.presentation.navigation.LocalSnackbarHostState
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.CalendarPlus
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsState()
    val events by viewModel.events.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    var selectedDate by remember {mutableStateOf(LocalDate.now())}
    var showEventDialog by remember { mutableStateOf<Event?>(null) }
    val scrollState = rememberScrollState()

    val localSnackbarHostState = LocalSnackbarHostState.current
    var snackbarMessage: String? by remember { mutableStateOf(null) }
    Log.d("화면렌더링", "BasicPagerCalendar: ")
    LaunchedEffect(snackbarMessage) {
        if (!snackbarMessage.isNullOrEmpty()) {
            localSnackbarHostState.showSnackbar(message = snackbarMessage!!)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
    ) {
        Text(
            text = "Shared Calendar",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            BasicPagerCalendar(
                selectedDate = selectedDate,
                onDateSelected = { date -> selectedDate = date },
                onChangedMonth = viewModel::onChangeMonth,
                markedDate = events.mapNotNull {
                    LocalDate.parse(
                        it.date,
                        DateTimeFormatter.ISO_LOCAL_DATE
                    )
                }
            )
        }


        AnimatedVisibility(
            visible = selectedDate.year == selectedMonth.year && selectedDate.monthValue == selectedMonth.monthValue,
            enter = fadeIn(
                animationSpec = tween(500),
                initialAlpha = 0f
            ),
            exit = fadeOut(
                animationSpec = tween(500),
                targetAlpha = 0f
            )
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceBright,
                tonalElevation = 4.dp,
                shadowElevation = 4.dp,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = viewModel.formatDate(selectedDate),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        TextButton(
                            onClick = {
                                showEventDialog = Event(
                                        uid = users.firstNotNullOf { it.uid },
                                        title = "",
                                        date = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE),
                                        fullday = true,
                                    )

                            },
//                        modifier = Modifier.align(Alignment.End),
                            colors = ButtonDefaults.buttonColors()
                                .copy(containerColor = MaterialTheme.colorScheme.primary)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(imageVector = Tabler.Outline.CalendarPlus, "")
                                Text(
                                    text = "Add Event",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }


                    val dailyEvents =
                        events.filter { it.date == selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE) }
                    if (dailyEvents.isEmpty()) {
                        Text(
                            text = "No events for this day",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        dailyEvents.forEach { event ->
                            EventCard(
                                modifier = Modifier.padding(vertical = 8.dp),
                                event = event,
                                author = users.firstOrNull { it.uid == event.uid }?.nickname ?: "UnKnown",
                                isOwner = event.uid == users[0].uid,
                                onEdit = { showEventDialog = event },
                                onDelete = { viewModel.deleteEvent(event) },
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
    if (showEventDialog != null) {
        EditEventDialog(
            event = showEventDialog!!,
            onSubmit = { viewModel.submitEvent(it) },
            onDismiss = { showEventDialog = null },
            onError = { snackbarMessage = it }
        )
    }
}
package com.self.lovenotes.ui.calendar

import BasicPagerCalendar
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
import com.self.lovenotes.data.model.Event
import com.self.lovenotes.ui.navigation.LocalSnackbarHostState
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Outline
import com.woowla.compose.icon.collections.tabler.tabler.outline.CalendarPlus
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsState()
    val events by viewModel.events.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val showEventDialog by viewModel.showEventDialog.collectAsState()
    val scrollState = rememberScrollState()

    val localSnackbarHostState = LocalSnackbarHostState.current
    var snackbarMessage: String? by remember { mutableStateOf(null) }

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
                selectedDate = LocalDate.parse(selectedDate, DateTimeFormatter.ISO_LOCAL_DATE),
                onDateSelected = { date -> viewModel.selectDate(date.format(DateTimeFormatter.ISO_LOCAL_DATE)) }
            )
        }

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
                            viewModel.showEditEventDialog(
                                event = Event(
                                    uid = users.keys.toList()[0],
                                    title = "",
                                    date = selectedDate,
                                    fullday = true,
                                )
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

                val dailyEvents = events.filter { it.date == selectedDate }
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
                            author = users[event.uid]?.nickname ?: "UnKnown",
                            onEdit = { viewModel.showEditEventDialog(event) },
                            onDelete = { viewModel.deleteEvent(event) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    if (showEventDialog != null) {
        EditEventDialog(
            event = showEventDialog!!,
            onSubmit = { viewModel.submitEvent(it) },
            onDismiss = { viewModel.showEditEventDialog(null) },
            onError = { snackbarMessage = it }
        )
    }
}
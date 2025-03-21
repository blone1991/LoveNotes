package com.self.lovenotes.ui.Calendar

import BasicPagerCalendar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.self.lovenotes.data.model.Event
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = hiltViewModel(),
) {
    val users by viewModel.users.collectAsState()
    val events by viewModel.events.collectAsState()

    val selectedDate by viewModel.selectedDate.collectAsState()
    val popupDialog by viewModel.popupDialog.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect (selectedDate) {
        viewModel.fetchEvents()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
//            .padding(16.dp)
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
                Text(
                    text = viewModel.formatDate(selectedDate),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (events.isEmpty()) {
                    Text(
                        text = "No events for this day",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else {
                    events.forEach { event ->
//                        Column (
//                            modifier = Modifier.padding(4.dp),
//                        ){
//                            Text(
//                                text = event.title + "- ${users[event.uid]?.nickname ?: "UnKnown"}",
//                                style = MaterialTheme.typography.bodyLarge,
//                                color = MaterialTheme.colorScheme.onSurface,
//                                modifier = Modifier.padding(top = 4.dp)
//                            )
//                        }
                        EventCard(
                            modifier = Modifier.padding(4.dp),
                            event = event,
                            author = users[event.uid]?.nickname ?: "UnKnown"
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = { viewModel.showDialog(true) },
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.buttonColors()
                        .copy(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Add Event",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    if (popupDialog) {
        AddEventDialog(
            event = Event(
                uid = users.keys.toList()[0],
                title = "",
                date = selectedDate,
                fullday = true,
            ),
            onSubmit = { viewModel.submitEvent(it) },
            onClose = { viewModel.showDialog(false) }
        )
    }
}
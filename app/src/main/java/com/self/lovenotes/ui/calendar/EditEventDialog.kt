package com.self.lovenotes.ui.calendar

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.self.lovenotes.data.model.Event
import com.self.lovenotes.ui.common.DatePickerDialog
import com.self.lovenotes.ui.common.TimePickerDialog
import com.woowla.compose.icon.collections.tabler.Tabler
import com.woowla.compose.icon.collections.tabler.tabler.Filled
import com.woowla.compose.icon.collections.tabler.tabler.filled.Clock
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun EditEventDialog(
    event: Event,
    onError: (String) -> Unit = {},
    onSubmit: (Event) -> Unit,
    onDismiss: () -> Unit,
) {
    var _event by remember { mutableStateOf(event.copy()) }

    var datePickerDialogDisplay by remember { mutableStateOf(false) }
    var startTimePickerDialogDisplay by remember { mutableStateOf(false) }
    var endTimePickerDialogDisplay by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Surface(
            modifier = Modifier,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceBright,
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = if (event.id.isEmpty()) "Add New Event" else "Edit Event",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = if (event.id.isEmpty()) "Create a new event to share on your calendar." else "You can modify the registered event",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Title",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                OutlinedTextField(
                    value = _event.title,
                    isError = _event.title.isEmpty(),
                    onValueChange = { _event = _event.copy(title = it)},
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {Icon(imageVector = Icons.Default.Favorite, "")}
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Date",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedTextField (
                    value = _event.date,
                    onValueChange = {},
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            when (it.hasFocus) {
                                true -> {
                                    datePickerDialogDisplay = true
                                }

                                false -> {}
                            }
                        }
                        .focusable(),
                    leadingIcon = {Icon(imageVector = Icons.Default.DateRange, "")}
                )

//                Spacer(modifier = Modifier.height(10.dp))

                Row (
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        "is All Day Schedule",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Checkbox(checked = _event.fullday, onCheckedChange = {
                        if (!it) {
                            _event = _event.copy(fullday = it, startTime = "0000", endTime = "2359")
                        } else {
                            _event = _event.copy(fullday = it)
                        }
                    })
                }

                if (!_event.fullday) {
                    Text(
                        "Time",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelLarge
                    )
                    // 시작 시간
                    Row (
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Icon(imageVector = Tabler.Filled.Clock, contentDescription = "")
                        OutlinedTextField (
                            value = _event.startTime.substring(0,2) + ":" + _event.startTime.substring(2,4),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged {
                                    when (it.hasFocus) {
                                        true -> {
                                            startTimePickerDialogDisplay = true
                                        }

                                        false -> {}
                                    }
                                }
                                .focusable()
                        )
                        Text(text = "~")
                        OutlinedTextField (
                            value = _event.endTime.substring(0,2) + ":" + _event.endTime.substring(2,4),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .onFocusChanged {
                                    when (it.hasFocus) {
                                        true -> {
                                            endTimePickerDialogDisplay = true
                                        }

                                        false -> {}
                                    }
                                }
                                .focusable()
                        )
                    }
                }

                Row (
                    modifier = Modifier.align(Alignment.End),
                    ){
                    TextButton(
                        onClick = {
                            if (_event.title.isEmpty() ){
                                onError ("Input Title")
                                return@TextButton
                            }

                            if (_event.startTime.toInt() > _event.endTime.toInt()) {
                                onError ("Check the Schedule")
                                return@TextButton
                            }

                            onSubmit(_event)
                            onDismiss(); },
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
//                        colors = ButtonDefaults.buttonColors()
//                            .copy(containerColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Text(
                            text = "Confirm",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.small,
//                        colors = ButtonDefaults.buttonColors()
//                            .copy(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = "Cancel",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }

        if (startTimePickerDialogDisplay) {
            TimePickerDialog(onDismiss = { startTimePickerDialogDisplay = false },
                onConfirm = { hour, minute ->
                    _event = _event.copy(startTime = String.format("%02d%02d",hour, minute))
                    startTimePickerDialogDisplay = false }
            )
        }
        if (endTimePickerDialogDisplay) {
            TimePickerDialog(
                onDismiss = { endTimePickerDialogDisplay = false },
                onConfirm = { hour, minute ->
                    _event = _event.copy(endTime = String.format("%02d%02d",hour, minute))
                    endTimePickerDialogDisplay = false }
            )
        }

        if (datePickerDialogDisplay) {
            DatePickerDialog(
                selectedDate = LocalDate.parse(
                    _event.date,
                    DateTimeFormatter.ISO_LOCAL_DATE
                ),
                onDismiss = { datePickerDialogDisplay = false },
                onDateSelected = {_event = _event.copy(date = it.format(DateTimeFormatter.ISO_LOCAL_DATE))}
            )
        }
    }
}
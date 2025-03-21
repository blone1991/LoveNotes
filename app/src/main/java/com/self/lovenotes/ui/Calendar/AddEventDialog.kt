package com.self.lovenotes.ui.Calendar

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import com.self.lovenotes.ui.Common.TimePickerDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    event: Event,
    onSubmit: (Event) -> Unit,
    onClose: () -> Unit,
) {
    var _event by remember { mutableStateOf(event.copy()) }

    var startTimePickerDialogDisplay by remember { mutableStateOf(false) }
    var endTimePickerDialogDisplay by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onClose,
    ) {
        Surface(
            modifier = Modifier,
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceBright,
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Add New Event",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Create a new event to share on your calendar.",
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
                    onValueChange = { _event = _event.copy(title = it)},
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth()
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
                    Checkbox(checked = _event.fullday, onCheckedChange = { _event = _event.copy(fullday = it)})
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){

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
                        Text(text = "~", Modifier.padding(horizontal = 15.dp))
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

                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        if (_event.title.isEmpty() || _event.startTime.toInt() > _event.endTime.toInt()) {
                            return@Button
                        }

                        onSubmit(_event)
                        onClose();
                    },
                ) {
                    Text("Submit")
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
    }
}
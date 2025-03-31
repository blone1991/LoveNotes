package com.self.lovenotes.presentation.calendar

import android.util.Log
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.gms.maps.model.LatLng
import com.self.lovenotes.data.remote.model.Event
import com.self.lovenotes.presentation.common.DatePickerDialog
import com.self.lovenotes.presentation.common.MapPickerDialog
import com.self.lovenotes.presentation.common.TimePickerDialog
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
    var showMapDialog by remember { mutableStateOf(false) } // 지도 다이얼로그 표시 상태

    var datePickerDialogDisplay by remember { mutableStateOf(false) }
    var startTimePickerDialogDisplay by remember { mutableStateOf(false) }
    var endTimePickerDialogDisplay by remember { mutableStateOf(false) }

    val focusRequester = remember { FocusRequester() }
    val scrollState: ScrollState = rememberScrollState()

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
                    .verticalScroll(scrollState)
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
                    onValueChange = { _event = _event.copy(title = it) },
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(imageVector = Icons.Default.Favorite, "") }
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    "Date",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedTextField(
                    value = _event.date,
                    onValueChange = {},
                    label = { Text("Date") },
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged {
                            if (it.hasFocus) {
                                datePickerDialogDisplay = true
                                focusRequester.freeFocus()
                            }
                        }
                        .focusable(),
                    leadingIcon = { Icon(imageVector = Icons.Default.DateRange, "") }
                )

//                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    Row(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Tabler.Filled.Clock, contentDescription = "")
                        OutlinedTextField(
                            value = _event.startTime.substring(
                                0,
                                2
                            ) + ":" + _event.startTime.substring(2, 4),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (it.hasFocus) {
                                        startTimePickerDialogDisplay = true
                                        focusRequester.freeFocus()
                                    }
                                }
                                .focusable()
                        )
                        Text(text = "~")
                        OutlinedTextField(
                            value = _event.endTime.substring(0, 2) + ":" + _event.endTime.substring(2, 4),
                            onValueChange = {},
                            readOnly = true,
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .onFocusChanged {
                                    if (it.hasFocus) {
                                        endTimePickerDialogDisplay = true
                                        focusRequester.freeFocus()
                                    }
                                }
                                .focusable()
                        )
                    }
                }

                // Location 입력 필드 추가
                Text(
                    "Location",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = _event.location?.split("/")?.get(0) ?: "",
                        onValueChange = {},
                        label = { Text("Location") },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.hasFocus) {
                                    showMapDialog = true
                                    focusRequester.freeFocus()
                                }
                            },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "location"
                            )
                        },
                        textStyle = TextStyle().copy(fontSize = 10.sp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.align(Alignment.End),
                ) {
                    TextButton(
                        onClick = {
                            if (_event.title.isEmpty()) {
                                onError("Input Title")
                                return@TextButton
                            }

                            if (_event.startTime.toInt() > _event.endTime.toInt()) {
                                onError("Check the Schedule")
                                return@TextButton
                            }

                            onSubmit(_event)
                            onDismiss()
                        },
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

                // 지도 다이얼로그 표시
                if (showMapDialog) {
                    MapPickerDialog(
                        onDismiss = { showMapDialog = false },
                        location =
                        try {
                            val split = _event.location!!.split("/")
                            LatLng(split[1].toDouble(), split[2].toDouble())
                        } catch (e: Exception) {
                            Log.e("EditEventDialog", "EditEventDialog: ", e)
                            null
                        },
                        onLocationSelected = { latLng, address ->
                            _event = _event.copy(
                                location = "$address/${latLng.latitude}/${latLng.longitude}"
                            )
                            showMapDialog = false
                        }
                    )
                }
            }
        }

        if (startTimePickerDialogDisplay) {
            TimePickerDialog(onDismiss = { startTimePickerDialogDisplay = false },
                onConfirm = { hour, minute ->
                    _event = _event.copy(startTime = String.format("%02d%02d", hour, minute))
                    startTimePickerDialogDisplay = false
                }
            )
        }
        if (endTimePickerDialogDisplay) {
            TimePickerDialog(
                onDismiss = { endTimePickerDialogDisplay = false },
                onConfirm = { hour, minute ->
                    _event = _event.copy(endTime = String.format("%02d%02d", hour, minute))
                    endTimePickerDialogDisplay = false
                }
            )
        }

        if (datePickerDialogDisplay) {
            DatePickerDialog(
                selectedDate = LocalDate.parse(
                    _event.date,
                    DateTimeFormatter.ISO_LOCAL_DATE
                ),
                onDismiss = { datePickerDialogDisplay = false },
                onDateSelected = {
                    _event = _event.copy(date = it.format(DateTimeFormatter.ISO_LOCAL_DATE))
                }
            )
        }
    }
}
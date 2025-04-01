package com.self.lovenotes.presentation.memory

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.presentation.common.DatePickerDialog
import com.self.lovenotes.presentation.common.ImagePickerComposable
import com.self.lovenotes.presentation.common.ToggleChip
import com.self.lovenotes.presentation.common.utils
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TrackingResultScreen(
    sharables: List<User>,
    dateMemory: DateMemory,
    onSave: (DateMemory) -> Unit,
    onClose: () -> Unit,
) {

    var date by remember {
        mutableStateOf(dateMemory.date.ifEmpty {
            LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        })
    }
    var memo by remember { mutableStateOf("") }
    var selectedPhotoBitmap by remember {
        mutableStateOf(
            dateMemory.photoBase64.mapNotNull { utils.base64ToBitmap(it) }
        )
    }
    var selectedShareWith by remember { mutableStateOf(dateMemory.shareWith) }

    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    var datePickerDialogDisplay by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            utils.getRotatedBitmapFromUri(context, uri)?.let {
                selectedPhotoBitmap += it
            }
        }
    }

    var cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(dateMemory) {
        if (dateMemory.geoList.isNotEmpty()) {
            dateMemory.getLatLngList().let { latLngs ->
                cameraPositionState = cameraPositionState.apply {
                    val latitudeList = latLngs.map { it.latitude }.sorted()
                    val longitudeList = latLngs.map { it.longitude }.sorted()

                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            (latitudeList.first() + latitudeList.last()) / 2,
                            (longitudeList.first() + longitudeList.last()) / 2
                        ), 15f
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(top = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    "Date",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )

                OutlinedTextField(
                    value = date,
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

                Text(
                    text = "Add Note",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                OutlinedTextField(
                    value = memo,
                    onValueChange = { memo = it },
                    label = { Text("notes") },
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Share With",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
                LazyRow {
                    items(sharables) { user ->
                        ToggleChip(
                            selected = selectedShareWith.contains(user.uid),
                            onClick = {
                                if (selectedShareWith.contains(user.uid))
                                    selectedShareWith = selectedShareWith.filter { it != user.uid }
                                else
                                    selectedShareWith += user.uid
                            },
                            label = { Text(user.nickname) },
                            selectedLeadingIcon = { Icon(Icons.Default.Check, "") },
                            color = MaterialTheme.colorScheme.primary,
                            selectedColor = Color.Green
                        )
                    }
                }

            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Dating Route\n")
                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    cameraPositionState = cameraPositionState,
                ) {
                    val LatLngList = dateMemory.getLatLngList()
                    if (LatLngList.isNotEmpty()) {
                        Marker(
                            state = MarkerState(position = LatLngList[0]),
                            title = "Start"
                        )

                        Marker(
                            state = MarkerState(position = LatLngList[LatLngList.size -1]),
                            title = "End"
                        )

                        Polyline(
                            points = LatLngList,
                            color = Color.Red,
                            width = 20f,
                            zIndex = 2f
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surfaceBright,
            tonalElevation = 4.dp,
            shadowElevation = 4.dp,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Add Memorial\n")
                LazyRow(
                    modifier = Modifier,
                    contentPadding = PaddingValues(horizontal = 4.dp), // 추가: contentPadding 설정
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(3) { i ->
                        selectedPhotoBitmap.getOrNull(i)?.let {
                            ImagePickerComposable(
                                width = 200.dp,
                                height = 200.dp,
                                bitmap = it.asImageBitmap(),
                                modifier = Modifier,
                                onCancel = {selectedPhotoBitmap =
                                    selectedPhotoBitmap.filter { bitmap -> bitmap != it }},
//                                painter = rememberAsyncImagePainter(it),
                                contentDescription = "",
                                contentScale = ContentScale.Fit // 추가: 이미지 비율 유지
                            )
                        } ?: Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(200.dp)
                                .border(
                                    border = BorderStroke(
                                        2.dp,
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                                    ),
                                    shape = RectangleShape
                                )
                                .clickable {
                                    photoPicker.launch(
                                        input = PickVisualMediaRequest
                                            .Builder()
                                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                            .build()
                                    )
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                modifier = Modifier.size(50.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                imageVector = Icons.Default.Add,
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            Button(
                onClick = {
                    onSave(
                        dateMemory.copy(
                            date = date,
                            shareWith = selectedShareWith,
                            photoBase64 = selectedPhotoBitmap.map { utils.bitmapToBase64(it) },
                            memo = memo
                        )
                    )
                    onClose()
                },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text("Save")
            }
        }
    }

    if (datePickerDialogDisplay) {
        DatePickerDialog(
            selectedDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE),
            onDismiss = { datePickerDialogDisplay = false },
            onDateSelected = { date = it.format(DateTimeFormatter.ISO_LOCAL_DATE) }
        )
    }
}
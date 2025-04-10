@file:OptIn(ExperimentalFoundationApi::class)

package com.self.lovenotes.presentation.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.self.lovenotes.data.remote.model.DateMemory
import kotlinx.coroutines.launch

@Composable
fun MemoryCard(
    memory: DateMemory,
    isOwner: Boolean,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(if (isExpanded) 15.dp else 4.dp, label = "")

    val cameraPositionState = rememberCameraPositionState()

//    val coroutineScope = rememberCoroutineScope()
    val movingMarkerValue = remember { Animatable(0f, 1f) }
    val expandValue = remember { Animatable(0f) }

    LaunchedEffect (isExpanded) {
        launch {
            if (isExpanded) {
                movingMarkerValue.animateTo(
                    targetValue = (memory.getLatLngList().size - 1).toFloat(),
                    animationSpec = tween(8000, 0, easing = LinearEasing)
                )
            } else {
                movingMarkerValue.snapTo(0f)
            }
        }

        launch {
            if (isExpanded) {
                expandValue.animateTo(
                    targetValue = 10f,
                    animationSpec = tween(500, 0, LinearEasing)
                )
            } else {
                expandValue.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(500, 0, LinearEasing)
                )
            }
        }
    }

    val movingValue = remember { Animatable(0f) }
    val padingValue = remember { Animatable(0f) }

    LaunchedEffect (Unit) {
        launch { movingValue.animateTo(100f, tween(300)) }
        launch { padingValue.animateTo(1f, tween(500)) }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x= (100 - movingValue.value).dp)
            .alpha(padingValue.value)
            .shadow(elevation, shape = RoundedCornerShape(12.dp))
            .combinedClickable(
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick
            ) {
                isExpanded = !isExpanded
            },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 날짜
                Text(
                    text = memory.date,
                    style = MaterialTheme.typography.headlineLarge
                )

                if (isOwner) {
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }

                        IconButton(onClick = onDelete) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }

            // 메모
            if (memory.memo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "***Note***\n${memory.memo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                cameraPositionState = cameraPositionState.apply {
                    val latLngList = memory.getLatLngList()
                    if (latLngList.isNotEmpty()) {
                        val latitudeList = latLngList.map { it.latitude }.sorted()
                        val longitudeList = latLngList.map { it.longitude }.sorted()

                        position = CameraPosition.fromLatLngZoom(
                            LatLng(
                                (latitudeList.first() + latitudeList.last()) / 2,
                                (longitudeList.first() + longitudeList.last()) / 2
                            ), 15f
                        )
                    } else {
                        position = CameraPosition.fromLatLngZoom(
                            LatLng(37.5665, 126.9780), 12f
                        )
                    }
                },
                uiSettings = MapUiSettings().copy(
                    compassEnabled = false,
                    indoorLevelPickerEnabled = false,
                    myLocationButtonEnabled = false,
                    rotationGesturesEnabled = false,
                    scrollGesturesEnabled = false,
                    scrollGesturesEnabledDuringRotateOrZoom = true,
                    tiltGesturesEnabled = false,
                    zoomControlsEnabled = true,
                    zoomGesturesEnabled = true
                )
            ) {
                val LatLngList = memory.getLatLngList()

                if (LatLngList.isNotEmpty()) {

                    MarkerComposable(
                        state = MarkerState(position = LatLngList[
                                movingMarkerValue.value.toInt()
                        ]),
                        title = "Ours",

                    ) {
                        Text(text = "😍")
                    }

                    Polyline(
                        points = LatLngList,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        width = 20f,
                        zIndex = 2f
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded && memory.photoBase64.isNotEmpty(),
                enter = expandVertically(
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                ) + fadeIn(animationSpec = tween(1000)), // 페이드 인 추가
                exit = shrinkVertically(
                    animationSpec = tween(durationMillis = 1000, easing = LinearEasing)
                ) + fadeOut(animationSpec = tween(1000)) // 페이드 아웃 추가
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    items(memory.photoBase64.mapNotNull {
                        utils.base64ToBitmap(it)?.asImageBitmap()
                    }) {
                        Image (
                            bitmap = it,
                            contentDescription = "Memory Photo",
                            modifier = Modifier
                                .width(300.dp)
                                .height(300.dp),
                            contentScale = ContentScale.Fit // 추가: 이미지 비율 유지
                        )
                    }
                }
            }
        }
    }
}



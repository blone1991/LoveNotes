@file:OptIn(ExperimentalFoundationApi::class)

package com.self.lovenotes.presentation.common

import android.util.Log
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
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.self.lovenotes.data.remote.model.DateMemory
import kotlinx.coroutines.launch
import kotlin.math.log

@Composable
fun MemoryCard(
    memory: DateMemory,
    isOwner: Boolean,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
) {

    Log.d("화면렌더링", "MemoryCard: ${memory.id}")
    val cameraPositionState = rememberCameraPositionState()
    var isExpanded by remember(memory.id) { mutableStateOf(false) }
    val movingMarkerValue = remember(memory.id) { Animatable(0f, 1f) }

    val latLngList = remember(memory.id) { memory.getLatLngList() }
    val safeIndex = movingMarkerValue.value.toInt().coerceIn(0, latLngList.lastIndex)

    val images = remember(memory.id) {
        memory.photoBase64.mapNotNull {
            utils.base64ToBitmap(it)?.asImageBitmap()
        }
    }

    // 초기 위치 설정
    LaunchedEffect(memory.id) {
        if (latLngList.isNotEmpty()) {
            val latitudeList = latLngList.map { it.latitude }.sorted()
            val longitudeList = latLngList.map { it.longitude }.sorted()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(
                LatLng(
                    (latitudeList.first() + latitudeList.last()) / 2,
                    (longitudeList.first() + longitudeList.last()) / 2
                ), 14f
            )
        } else {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(37.5665, 126.9780), 12f)
        }
    }

    // 애니메이션 효과
    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            movingMarkerValue.animateTo(latLngList.lastIndex.toFloat(), tween(8000, 0, LinearEasing))
        } else {
            movingMarkerValue.snapTo(0f)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp)
            .combinedClickable(
                onClick = { isExpanded = !isExpanded },
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick
            ),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceBright)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = memory.date, style = MaterialTheme.typography.headlineLarge)

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
                cameraPositionState = cameraPositionState,
                uiSettings = MapUiSettings(
                    compassEnabled = false,
                    indoorLevelPickerEnabled = false,
                    myLocationButtonEnabled = false,
                    rotationGesturesEnabled = false,
                    tiltGesturesEnabled = false,
                )
            ) {
                if (latLngList.isNotEmpty()) {
                    MarkerComposable(
                        state = MarkerState(position = latLngList[safeIndex]),
                        title = "Ours"
                    ) {
                            Text(text = "😍")
                    }

                    Polyline(
                        points = latLngList,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        width = 20f,
                        zIndex = 2f
                    )
                }
            }

            AnimatedVisibility(
                visible = isExpanded && images.isNotEmpty(),
                enter = expandVertically(animationSpec = tween(1000)) + fadeIn(animationSpec = tween(1000)),
                exit = shrinkVertically(animationSpec = tween(1000)) + fadeOut(animationSpec = tween(1000))
            ) {
                LazyRow {
                    items(images) {
                        Image(
                            bitmap = it,
                            contentDescription = "Memory Photo",
                            modifier = Modifier
                                .width(300.dp)
                                .height(300.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}




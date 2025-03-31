@file:OptIn(ExperimentalFoundationApi::class)

package com.self.lovenotes.presentation.memory

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.data.remote.model.User
import com.self.lovenotes.presentation.common.utils

@Composable
fun MemoryCard(
    memory: DateMemory,
    onEdit: (() -> Unit)? = null,
    onDelete: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
    onDoubleClick: (() -> Unit)? = null,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(if (isExpanded) 15.dp else 4.dp, label = "")

    val cameraPositionState = rememberCameraPositionState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
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

                Row {
                    onEdit?.let {
                        IconButton(onClick = it) {
                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit")
                        }
                    }

                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
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
                            ), 12f
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
            )

            if (isExpanded && memory.photoBase64.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(memory.photoBase64.mapNotNull {
                        utils.base64ToBitmap(it)?.asImageBitmap()
                    }) {
                        Image(
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



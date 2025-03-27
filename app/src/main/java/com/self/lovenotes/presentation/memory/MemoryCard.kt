package com.self.lovenotes.presentation.memory

import android.graphics.BitmapFactory
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.self.lovenotes.data.remote.model.DateMemory
import java.util.Base64

@Composable
fun MemoryCard(memory: DateMemory) {
    var isExpanded by remember { mutableStateOf(false) }
    val elevation by animateDpAsState(if (isExpanded) 15.dp else 4.dp, label = "")

    val cameraPositionState = rememberCameraPositionState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation, shape = RoundedCornerShape(12.dp))
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // 날짜
            Text(
                text = memory.date,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary
            )

            GoogleMap(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                cameraPositionState = cameraPositionState.apply {
                    val latitudeList =
                        memory.getCoordinateList().map {
                            it.latitude
                        }.sorted()

                    val longitudeList =
                        memory.getCoordinateList().map {
                            it.longitude
                        }.sorted()

                    position = CameraPosition.fromLatLngZoom(
                        LatLng(
                            (latitudeList.first() + latitudeList.last()) / 2,
                            (longitudeList.first() + longitudeList.last()) / 2
                        ), 12f
                    )
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

            // 메모
            if (memory.memo.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = memory.memo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (isExpanded && memory.photoBase64.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(memory.photoBase64) {
                        Image(
                            bitmap = base64ToBitmap(it).asImageBitmap(),
                            contentDescription = "Memory Photo",
                            modifier = Modifier
                                .fillMaxWidth()
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

private fun base64ToBitmap(base64: String): android.graphics.Bitmap {
    val decodedBytes = Base64.getDecoder().decode(base64)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}
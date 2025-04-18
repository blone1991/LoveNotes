package com.self.lovenotes.presentation.common

import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun MapPickerDialog(
    onDismiss: () -> Unit,
    location: LatLng? = null,
    onLocationSelected: (LatLng, String) -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            location ?: LatLng(37.5665, 126.9780),  // 서울 기본 위치
            11f
        )
    }
    var selectedLatLng by remember { mutableStateOf(location) }
    var inputAddress by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Location") },
        text = {
            Column {
                Row {
                    TextField(
                        value = inputAddress,
                        label = { Text(text = "주소 검색")},
                        onValueChange = {inputAddress = it},
                        trailingIcon = {
                            IconButton(onClick = {
                                getLatLngFromAddress(context, inputAddress)?.let {
                                    selectedLatLng = it
                                    cameraPositionState.apply { position = CameraPosition.fromLatLngZoom(
                                        it, 12f
                                    )}
                                }
                                inputAddress = ""
                            }) { Icon(Icons.Default.Search, "") }
                        }
                    )
                }

                GoogleMap(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                    onMapClick = { latLng ->
                        selectedLatLng = latLng
                    }
                ) {
                    selectedLatLng?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Selected Location"
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selectedLatLng?.let { latLng ->
                        coroutineScope.launch {
                            val address = getAddressFromLatLng(context, latLng) // 주소를 가져오는 함수 필요
                            onLocationSelected(latLng, address)
                        }
                    } ?: onDismiss()
                },
                enabled = selectedLatLng != null
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
    } catch (e: Exception) {
        "Error fetching address: ${e.message}"
    }
}

fun getLatLngFromAddress(context: Context, address: String): LatLng? {
    val geocoder = Geocoder(context, Locale.getDefault())
    return try {
        val addresses = geocoder.getFromLocationName(address, 1) // 최대 1개 결과 반환
        val location = addresses?.firstOrNull()
        location?.let {
            LatLng(it.latitude, it.longitude)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null // 주소 변환 실패 시 null 반환
    }
}
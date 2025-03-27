package com.self.lovenotes.presentation.memory

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun TrackingResultScreen(
    viewModel: TrackingResultViewModel = hiltViewModel(),
    onSave: () -> Unit,
) {
    val path by viewModel.path.collectAsState()
    var memo by remember { mutableStateOf("") }
    var selectedPhotoUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    val context = LocalContext.current

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickMultipleVisualMedia(3) // 최대 3장 제한
    ) { uris ->
        selectedPhotoUris = uris
    }

    var cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(Unit) {
        viewModel.loadPath()
    }

    LaunchedEffect(path) {
        if (path.isNotEmpty()) {
            path[0].getLatLng()?.let {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 15f)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            cameraPositionState = cameraPositionState
        ) {
            Polyline(
                points = path.mapNotNull { it.getLatLng() },
                color = Color.Red,
                width = 20f,
                zIndex = 2f
            )
        }

        Button(
            onClick = {
                photoPicker.launch(
                    input = PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        .setMaxItems(3)
                        .build()
                )
            }
        ) {
            Text("Select Photos (Max 3)")
        }

        if (selectedPhotoUris.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .padding(top = 8.dp),
//                    .fillMaxWidth(), // 변경: LazyRow의 가로 크기를 fillMaxWidth()로 설정
                contentPadding = PaddingValues(horizontal = 16.dp) // 추가: contentPadding 설정
            ) {
                items(selectedPhotoUris) { uri ->
                    Image(
                        modifier = Modifier
                            .width(300.dp) // 변경: 이미지 너비 지정
                            .height(300.dp), // 변경: 이미지 높이 지정
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        contentScale = ContentScale.Fit // 추가: 이미지 비율 유지
                    )
                }
            }
        }


        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("Memo") },
            modifier = Modifier.padding(top = 8.dp)
        )

        Button(
            onClick = { viewModel.saveToFirestore(context, selectedPhotoUris, memo) },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Save")
        }
    }
}
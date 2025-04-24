package com.self.lovenotes.presentation.memory.view

import BasicPagerCalendar
import android.Manifest
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.presentation.memory.DateMemoryViewModel
import com.self.lovenotes.presentation.common.MemoryCard
import com.self.lovenotes.presentation.memory.EditDateMemoryScreen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DateMemoryScreen(
    viewModel: DateMemoryViewModel = hiltViewModel(),
) {
    Log.d("화면렌더링", "DateMemoryScreen: ")
    val users by viewModel.users.collectAsState()
    val memories by viewModel.memories.collectAsState()
    val locationTrackingSession by viewModel.locationTrackingSession.collectAsState()

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val permissionLauncher = rememberMultiplePermissionsState(listOf(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    ))

    val onEditMemory by viewModel.onEditMemory.collectAsState()

    LaunchedEffect(permissionLauncher.allPermissionsGranted) {
        if (!permissionLauncher.allPermissionsGranted) {
            permissionLauncher.launchMultiplePermissionRequest()
        }
    }

    onEditMemory?.let {
        EditDateMemoryScreen(
            sharables = users.drop(1),
            dateMemory = it,
            onSave = { viewModel.updateMemory(it) },
            onClose = { viewModel.closeEditMemory() }
        )
    }
    if (onEditMemory != null) {

    } else {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
        ) {
            if (!permissionLauncher.allPermissionsGranted) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    Text(
                        text = "Please grant location and foreground service permissions.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                ) {
                    // 헤더
                    Text(
                        text = "Our Memories",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 8.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceBright,
                        tonalElevation = 4.dp,
                        shadowElevation = 4.dp,
                    ) {
                        BasicPagerCalendar(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            onChangedMonth = viewModel::fetchDateMemoryMonth,
                            markedDate = memories.map {
                                LocalDate.parse(
                                    it.date,
                                    DateTimeFormatter.ISO_LOCAL_DATE
                                )
                            }
                        )
                    }

                    memories
                        .filter { it.date == selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE) }
                        .sortedByDescending { it.timeStamp }.let {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    it.forEach { memory ->
                                        MemoryCard(
                                            memory = memory,
                                            isOwner = memory.uid == users[0].uid,
                                            onEdit = { viewModel.openEditMemory(memory) },
                                            onDelete = { viewModel.deleteMemory(memory) },
                                        )
                                    }
                                }
                            }


                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd // 플로팅 버튼 위치 설정
        ) {
            FloatingActionButton(
                onClick = {
                    if (locationTrackingSession != null) {
                        viewModel.stopTracking();
                    } else {
                        viewModel.startTracking();
                    }
                },
                modifier = Modifier.padding(16.dp), // 여백 추가
                shape = CircleShape,
                containerColor = if (locationTrackingSession != null) Color.Red else MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (locationTrackingSession != null) Icons.Default.Share else Icons.Default.Add,
                        contentDescription = ""
                    )
                    Text(
                        modifier = Modifier.padding(end = 5.dp),
                        text = if (locationTrackingSession != null) "Stop" else "Add"
                    )
                }
            }
        }
    }
}
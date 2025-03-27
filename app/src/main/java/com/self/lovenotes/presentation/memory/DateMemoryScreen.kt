package com.self.lovenotes.presentation.memory.view

import android.Manifest
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.self.lovenotes.presentation.memory.DateMemoryViewModel
import com.self.lovenotes.presentation.memory.MemoryCard
import com.self.lovenotes.service.TrackingService

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DateMemoryScreen(
    navController: NavController,
    viewModel: DateMemoryViewModel = hiltViewModel(),
) {
    val memories by viewModel.memories.collectAsState()
    val isTracking by viewModel.isTracking.collectAsState()
    val context = LocalContext.current

    val permissions = listOf(
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        Manifest.permission.FOREGROUND_SERVICE,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    val permissionLauncher = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(permissionLauncher.allPermissionsGranted) {
        if (!permissionLauncher.allPermissionsGranted) {
            permissionLauncher.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.fetchMemories()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (isTracking) {
                        viewModel.stopTracking()
                        context.stopService(Intent(context, TrackingService::class.java))
                        navController.navigate("TrackingResult")
                    } else {
                        viewModel.startTracking()
                        context.startForegroundService(Intent(context, TrackingService::class.java))
                    }
                },
                shape = CircleShape,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = if (isTracking) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (isTracking) "Stop Tracking" else "Start Tracking"
                )
            }
        },
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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

                    // 추억 목록
                    if (memories.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No memories yet. Start tracking your journey!",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(memories.sortedByDescending { it.date }) { memory ->
                                MemoryCard(memory = memory)
                            }
                        }
                    }
                }
            }
        }
    }
}
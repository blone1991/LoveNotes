package com.self.lovenotes.presentation.login

import android.Manifest
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLogin: () -> Unit,
) {
    val users by viewModel.user.collectAsState()
    var permissionReady by remember { mutableStateOf(false) }

    val activity = LocalActivity.current


    val permissions =
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
//            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE,
        )
    
    val permissionLauncher = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(permissionLauncher.allPermissionsGranted) {
        if (permissionLauncher.allPermissionsGranted) {
            permissionReady = true
        } else {
            if (!permissionLauncher.shouldShowRationale)
                permissionLauncher.launchMultiplePermissionRequest()
        }
    }

    if (!permissionReady) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Permission was denied. Please Grant Permissions")
        }
        return
    } else if (users.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Login has Faild. Please Check Network And Retry Login")
            Button(onClick = viewModel::fetchUsers) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "재시도")
                Text("Retry")
            }
        }
        return
    } else {
        onLogin()
    }

}
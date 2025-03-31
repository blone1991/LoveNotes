package com.self.lovenotes.presentation.login

import android.Manifest
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.self.lovenotes.BuildConfig

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLogin: () -> Unit,
) {
    val users by viewModel.user.collectAsState()
    var permissionReady by remember { mutableStateOf(false) }
    val error by viewModel.error.collectAsState()

    val activity = LocalActivity.current

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            viewModel.signInWithGoogle(account.idToken!!, onError = viewModel::showError)
        } catch (e: ApiException) {
            viewModel.showError("Google 로그인 실패: ${e.message}")
        }
    }

    val permissions =
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
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
    } else if (users.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(BuildConfig.GOOGLE_AUTH_CLIENT_ID)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(activity!!, gso)
                    launcher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Google로 로그인")
            }
        }

        error?.let {
            AlertDialog(
                onDismissRequest = viewModel::clearError,
                confirmButton = { Button(onClick = viewModel::clearError) {
                    Text("Confirm", color = MaterialTheme.colorScheme.onPrimary)
                }},
                text = { Text(it, color = MaterialTheme.colorScheme.onErrorContainer) }
            )
        }
    } else {
        viewModel.fetchUsers()
        onLogin()
    }

}
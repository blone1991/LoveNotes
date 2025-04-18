package com.self.lovenotes.presentation.login

import android.Manifest
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.self.lovenotes.BuildConfig
import com.self.lovenotes.presentation.navigation.LocalSnackbarHostState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLogin : (Uri?) -> Unit = {}
) {
    val activity = LocalActivity.current
    val loginState by viewModel.loginState.collectAsState()
    val users by viewModel.users.collectAsState()
    val deepLink by viewModel.deepLink.collectAsState()

    // 앱 권한 요청
    val permissions =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
                Manifest.permission.POST_NOTIFICATIONS,
            )
        } else {
            mutableListOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.FOREGROUND_SERVICE,
            )
        }
    val permissionState = rememberMultiplePermissionsState(permissions = permissions)

    // 구글 로그인 런처
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            viewModel.signInWithGoogle(account.idToken!!, onError = viewModel::showError)
        } catch (e: ApiException) {
            viewModel.showError("Google 로그인 실패: ${e.message}")
        }
    }
    val localSnackbarHostState = LocalSnackbarHostState.current

    LaunchedEffect(Unit) {
        // DeepLink
        activity?.let {
            viewModel.handleDeepLink(it.intent);
        }

        // 1. 권한 확인 및 요청
        if (!permissionState.allPermissionsGranted) {
            permissionState.launchMultiplePermissionRequest()
        }
    }

    LaunchedEffect (permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            viewModel.checkLoginStatus()
        }
    }

    LaunchedEffect (users) {
        if (users.isNotEmpty()) {
            onLogin(deepLink)
        }
    }

    LaunchedEffect (Unit) {
        viewModel.errorFlow.collect {
            it?.let {
                localSnackbarHostState.showSnackbar(it)
            }
        }
    }

    when {
        permissionState.allPermissionsGranted == false -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Permission was denied. Please Grant Permissions")
            }
        }

        loginState is LoginState.Idle -> {
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
        }

        users.isEmpty() -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("유저 정보를 가지고 오는 중.")
            }
        }

    }
}

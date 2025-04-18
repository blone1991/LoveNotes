package com.self.lovenotes.presentation.login

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.self.lovenotes.data.remote.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class LoginState {
    object Idle : LoginState()
    object Success : LoginState()
}

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val userRepository: UserRepository,
    private val dynamicLinks: FirebaseDynamicLinks
) : ViewModel() {
    var _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _deepLink = MutableStateFlow<Uri?>(null)
    val deepLink = _deepLink.asStateFlow()

    val users = userRepository.userInfos

    var errorFlow = MutableSharedFlow<String?>()

    fun signInWithGoogle(idToken: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnFailureListener { onError(it.message ?: "Google 로그인 실패") }
        }
    }

    fun showError (error: String) = viewModelScope.launch {
        errorFlow.emit(error)
    }

    fun checkLoginStatus () {
        if (auth.currentUser == null) {
            _loginState.value = LoginState.Idle
        } else {
            _loginState.value = LoginState.Success
        }
    }

    fun handleDeepLink(intent: Intent) {
        viewModelScope.launch {
            dynamicLinks.getDynamicLink(intent)
                .addOnSuccessListener {pending ->
                    pending?.link?.let {
                        _deepLink.value = it
                        Log.d("LoginViewModel", "handleDeepLink: (get) $it")
                    }
                }.addOnFailureListener {
                    Log.d("LoginViewModel", "handleDeepLink: Deeplink get Fail")
                }
        }

    }
}
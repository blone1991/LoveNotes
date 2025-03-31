package com.self.lovenotes.presentation.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.self.lovenotes.data.remote.repository.UserRepository
import com.self.lovenotes.domain.usecase.CalendarUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val calendarUsecase: CalendarUsecase,
) : ViewModel() {
    val user = calendarUsecase.users.asStateFlow()

    private val _permissionReady = MutableStateFlow(false)
    private var _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        fetchUsers()
    }

    fun fetchUsers() {
        viewModelScope.launch { calendarUsecase.fetchUsers() }
    }

    fun signInWithGoogle(idToken: String, onError: (String) -> Unit) {
        viewModelScope.launch {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential)
                .addOnSuccessListener { fetchUsers() }
                .addOnFailureListener { onError(it.message ?: "Google 로그인 실패") }
        }
    }

    fun showError (error: String) {
        _error.value = error
    }

    fun clearError () {
        _error.value = null
    }
}
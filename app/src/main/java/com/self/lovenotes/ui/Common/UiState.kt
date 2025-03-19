package com.self.lovenotes.ui.Common

sealed class UiState {
    object Loading
    object Error
    data class Success(val code: Int)
}
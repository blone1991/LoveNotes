package com.self.lovenotes.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 라이트 모드 색상 스키마
private val LightColors = lightColorScheme(
    primary = LovePink,
    secondary = DeepPink,
    background = SoftWhite,
    surface = SoftWhite,
    onPrimary = OnPrimary,
    onSecondary = OnSurface,
    onBackground = OnSurface,
    onSurface = OnSurface,
    error = ErrorRed
)

// 다크 모드 색상 스키마 (선택적)
private val DarkColors = darkColorScheme(
    primary = DeepPink,
    secondary = LovePink,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onPrimary = OnPrimary,
    onSecondary = OnSurface,
    onBackground = Color(0xFFE0E0E0),
    onSurface = Color(0xFFE0E0E0),
    error = ErrorRed
)

@Composable
fun LoveNotesTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColors
    } else {
        LightColors
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = LoveShapes,
        content = content
    )
}
package com.self.lovenotes.presentation.common

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun ToggleChip(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    label: @Composable () -> Unit,
    color: Color = MaterialTheme.colorScheme.primary,
    selectedColor: Color = MaterialTheme.colorScheme.onPrimary,
    leadingIcon: @Composable (() -> Unit)? = null,
    selectedLeadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    selectedTrailingIcon: @Composable (() -> Unit)? = null,
) {
    AssistChip(
        modifier = modifier,
        label = label,
        onClick = onClick,
        colors = AssistChipDefaults.assistChipColors().copy(
            containerColor = if (selected) selectedColor.copy(alpha = 0.01f) else color.copy(alpha = 0.01f),
            labelColor = if (selected) selectedColor else color,
            leadingIconContentColor = if (selected) selectedColor else color,
            trailingIconContentColor = if (selected) selectedColor else color,
        ),
        leadingIcon = if (selected) selectedLeadingIcon else leadingIcon,
        trailingIcon = if (selected) selectedTrailingIcon else trailingIcon
    )
}
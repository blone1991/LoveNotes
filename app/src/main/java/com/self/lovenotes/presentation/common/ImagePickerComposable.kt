package com.self.lovenotes.presentation.common

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ImagePickerComposable (
    modifier: Modifier,
    width: Dp,
    height: Dp,
    bitmap: ImageBitmap,
    contentDescription: String = "",
    contentScale: ContentScale = ContentScale.Fit,
    onCancel: (() -> Unit)? = null
) {
    Box (
        modifier = modifier
            .width(width)
            .height(height),
    ){
        Image(
            bitmap = bitmap,
            modifier = modifier.fillMaxSize(),
            contentDescription = contentDescription,
            contentScale = contentScale // 추가: 이미지 비율 유지
        )
        onCancel?.let {
            IconButton(
                modifier = Modifier
                    .width(14.dp)
                    .height(14.dp)
                    .offset (x = width - 23.dp, y= 10.dp)
//                    .background(Color.Red.copy(alpha = 0.1f), shape = CircleShape)
                ,
                onClick = onCancel
            ) {
                Icon(imageVector = Icons.Default.Clear, contentDescription = "cancel")
            }
        }

    }
}
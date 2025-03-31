package com.self.lovenotes.presentation.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.util.Base64

object utils {
    fun bitmapToBase64(bitmap: Bitmap): String {
        val maxHeight = 300f
        val maxWidth = 300f
        val frameRatio = maxHeight / maxWidth

        val bitmapHeight = bitmap.height.toFloat()
        val bitmapWidth = bitmap.width.toFloat()
        val ratio = bitmapHeight / bitmapWidth

        val scaledBitmap = if (ratio > frameRatio) {
            Bitmap.createScaledBitmap(bitmap, (maxHeight / ratio).toInt(), 300, true) // 크기 조정
        } else {
            Bitmap.createScaledBitmap(bitmap, 300, (maxWidth / ratio).toInt(), true) // 크기 조정
        }

        val baos = ByteArrayOutputStream()

        scaledBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 50, baos)

        return Base64.getEncoder().encodeToString(baos.toByteArray())
    }

    fun base64ToBitmap(base64: String): android.graphics.Bitmap? =
        try {
            val decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)

            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            // Base64 디코딩 실패 처리
            e.printStackTrace()
            null
        }
}
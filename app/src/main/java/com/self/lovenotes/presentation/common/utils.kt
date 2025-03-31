package com.self.lovenotes.presentation.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.Base64

object utils {

    fun getRotatedBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        // 1. Uri로부터 비트맵 생성
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (bitmap == null) return null

        // 2. Uri로부터 EXIF 데이터 읽기
        val exifStream: InputStream? = context.contentResolver.openInputStream(uri)
        val exif = exifStream?.let { ExifInterface(it) }
        exifStream?.close()

        val orientation = exif?.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        ) ?: ExifInterface.ORIENTATION_NORMAL

        // 3. 회전 각도 계산
        val rotationAngle = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }

        // 4. 회전 적용
        return if (rotationAngle != 0f) {
            val matrix = Matrix().apply { postRotate(rotationAngle) }
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }

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
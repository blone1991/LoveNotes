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
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val bitmap = BitmapFactory.decodeStream(inputStream) ?: return null

                context.contentResolver.openInputStream(uri)?.use { exifStream ->
                    val exif = ExifInterface(exifStream)
                    val orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    val rotationAngle = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                        else -> 0f
                    }
                    if (rotationAngle != 0f) {
                        val matrix = Matrix().apply { postRotate(rotationAngle) }
                        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                    } else {
                        bitmap
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val maxWidth = 300f
        val maxHeight = 300f

        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()
        val scale = if (bitmapWidth > maxWidth || bitmapHeight > maxHeight) {
            minOf(maxWidth / bitmapWidth, maxHeight / bitmapHeight)
        } else {
            1f
        }

        val newWidth = (bitmapWidth * scale).toInt()
        val newHeight = (bitmapHeight * scale).toInt()

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        val baos = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.WEBP_LOSSLESS, 100, baos)
        return Base64.getEncoder().encodeToString(baos.toByteArray())
    }

    fun base64ToBitmap(base64: String): Bitmap? =
        try {
            val decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            null
        }
}
package com.self.lovenotes.data.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.time.Month
import java.time.YearMonth
import java.util.Base64

object utils {
    // "2025-03-XX" -> ("2025-03-01", "2025-03-31") 변환 헬퍼 함수
    fun getMonthRange(date: String): Pair<String, String> {
        val parts = date.split("-")
        val year = parts[0]
        val month = parts[1]
        val daysInMonth = java.time.YearMonth.of(year.toInt(), month.toInt()).lengthOfMonth()
        return ("$year-$month-01" to "$year-$month-$daysInMonth")
    }

    fun getMonthRange(yearMonth: YearMonth): Pair<String, String> {
        val year = yearMonth.year
        val month = String.format("%02d",yearMonth.monthValue)
        val daysInMonth = yearMonth.lengthOfMonth()
        return ("$year-$month-01" to "$year-$month-$daysInMonth")
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

    private fun base64ToBitmap(base64: String): android.graphics.Bitmap? =
        try {
            val decodedBytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)

            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: IllegalArgumentException) {
            // Base64 디코딩 실패 처리
            e.printStackTrace()
            null
        }
}
package com.self.lovenotes.presentation.memory

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.self.lovenotes.data.local.dao.PathDao
import com.self.lovenotes.data.local.entity.PathEntity
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.domain.usecase.CalendarUsecase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject


@HiltViewModel
class TrackingResultViewModel @Inject constructor(
    private val pathDao: PathDao,
    calendarUsecase: CalendarUsecase,
) : ViewModel() {

    private val _path = MutableStateFlow<List<PathEntity>>(emptyList())
    val path = _path.asStateFlow()

    private val users = calendarUsecase.users.asStateFlow()

    fun loadPath() {
        viewModelScope.launch {
            _path.value = pathDao.getPaths()
        }
    }

    fun saveToFirestore(context: Context, photoUris: List<Uri>, memo: String) {
        viewModelScope.launch {

            val photoBase64s = photoUris.map { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                bitmapToBase64(bitmap)
            }

            val memory = DateMemory(
                date = _path.value.get(0).date,
                uid = users.value.keys.toList()[0],
                geoList = _path.value.map { it.latlng },
                photoBase64 = photoBase64s,
                memo = memo,
            )

            Firebase.firestore.collection("date_memories").add(memory)
                .addOnSuccessListener {
                    viewModelScope.launch { pathDao.deletePaths() }
                }
        }
    }

    fun bitmapToBase64(bitmap: Bitmap): String {
        val maxWidth = 300
        val maxHeight = 300

        val width = bitmap.width
        val height = bitmap.height

        val ratioBitmap = width.toFloat() / height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
            .copy(bitmap.config!!, true)
        val baos = ByteArrayOutputStream()

        try {
            scaledBitmap.compress(Bitmap.CompressFormat.WEBP, 80, baos) // WEBP 압축, 품질 80으로 설정
            val byteArray = baos.toByteArray()

            // Android 버전에 따라 다른 Base64 인코더 사용
            return Base64.encodeToString(byteArray, Base64.DEFAULT)
        } finally {
            baos.close() // ByteArrayOutputStream 닫기
            scaledBitmap.recycle() // Bitmap 재활용
        }
    }
}
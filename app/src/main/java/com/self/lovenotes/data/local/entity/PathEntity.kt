package com.self.lovenotes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "gps_memory")
data class PathEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val timestamp: Long,
    val latlng: String,
    val sessionId: String,       // 같은 세션 구분용
) {
    fun getLatLng(): LatLng? =
        try {
            val splited = latlng.split(",")
            LatLng(splited[0].toDouble(), splited[1].toDouble())
        } catch (e: Exception) {
            null
        }

}



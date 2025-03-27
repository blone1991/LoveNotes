package com.self.lovenotes.data.remote.model

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot

data class DateMemory(
    val id: String = "",
    val uid: String = "",
    val date: String = "",
    val geoList: List<String>,
    val photoBase64: List<String> = emptyList(),
    val memo: String = "",
    val shareWith: List<String> = emptyList(),
) {
    constructor(document: DocumentSnapshot) : this(
        id = document.getString("id") ?: "",
        uid = document.getString("uid") ?: "",
        date = document.getString("date") ?: "",
        geoList = document.get("geoList") as? List<String> ?: emptyList(),
        photoBase64 = document.get("photoBase64") as? List<String> ?: emptyList(),
        memo = document.getString("memo") ?: "",
        shareWith = document.get("shareWith") as? List<String> ?: emptyList(),
    )

    fun getCoordinateList(): List<LatLng> =
        geoList.mapNotNull {
            try {
                val splited = it.split(",")
                LatLng(splited[0].toDouble(), splited[1].toDouble())
            } catch (e: Exception) {
                null
            }
        }
}


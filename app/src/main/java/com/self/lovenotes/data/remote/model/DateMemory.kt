package com.self.lovenotes.data.remote.model

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.android.parcel.Parcelize

data class DateMemory(
    val id: String = "",
    val uid: String = "",
    val date: String = "",
    val timeStamp: Long = 0,
    val geoList: List<String>,
    val photoBase64: List<String> = emptyList(),
    val memo: String = "",
    val shareWith: List<String> = emptyList(),
) {
    constructor(document: DocumentSnapshot) : this(
        id = document.getString("id") ?: "",
        uid = document.getString("uid") ?: "",
        date = document.getString("date") ?: "",
        timeStamp = document.getLong("timeStamp") ?: 0,
        geoList = document.get("geoList") as? List<String> ?: emptyList(),
        photoBase64 = document.get("photoBase64") as? List<String> ?: emptyList(),
        memo = document.getString("memo") ?: "",
        shareWith = document.get("shareWith") as? List<String> ?: emptyList(),
    )

    fun getLatLngList(): List<LatLng> {
        return geoList.mapNotNull {
            try {
                val splited = it.split(",")
                LatLng(splited[0].toDouble(), splited[1].toDouble())
            } catch (e: Exception) {
                null
            }
        }
    }

    fun toMap(): HashMap<String, Any?> = hashMapOf(
        "id" to id,
        "uid" to uid,
        "date" to date,
        "timeStamp" to timeStamp,
        "geoList" to geoList,
        "photoBase64" to photoBase64,
        "memo" to memo,
        "shareWith" to shareWith,
    )
}


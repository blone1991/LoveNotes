package com.self.lovenotes.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.Objects

data class Event(
    val id: String = "",
    val uid: String = "",
    val title: String,
    val date: String,
    val fullday: Boolean = true,
    val startTime: String = "0000",
    val endTime: String = "2359",
    val location: String? = null,  // 미사용
) {
    constructor(document: QueryDocumentSnapshot) : this(
        uid = document.getString("uid") ?: "",
        title = document.getString("title") ?: "",
        date = document.getString("date") ?: "",
        fullday = document.getBoolean("fullday") ?: true,
    )

    fun toMap() : HashMap<String, Any?> = hashMapOf(
        "id" to id,
        "uid" to uid,
        "title" to title,
        "date" to date,
        "fullday" to fullday,
        "startTime" to startTime,
        "endTime" to endTime,
        "location" to location,
    )
}

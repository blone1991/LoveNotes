package com.self.lovenotes.data.model

import com.google.firebase.firestore.QueryDocumentSnapshot
import java.util.UUID

data class Event(
    val id: String = UUID.randomUUID().toString(),
    val uid: String = "",
    val title: String,
    val date: String,
    val fullday: Boolean = true,
    val startTime: String = "0000",
    val endTime: String = "2359",
    val location: String? = null,  // 미사용
) {
    constructor(document: QueryDocumentSnapshot) : this(
        id = document.getString("id") ?: "",
        uid = document.getString("uid") ?: "",
        title = document.getString("title") ?: "",
        date = document.getString("date") ?: "",
        fullday = document.getBoolean("fullday") ?: true,
        startTime = document.getString("startTime") ?: "0000",
        endTime = document.getString("endTime") ?: "0000",
        location = document.getString("location")
    )

    fun toMap(): HashMap<String, Any?> = hashMapOf(
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

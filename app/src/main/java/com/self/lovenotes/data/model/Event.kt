package com.self.lovenotes.data.model

import com.google.firebase.Timestamp

data class Event(
    val uid: String,
    val title: String,
    val date: String,
    val allDay: Boolean = true,
    val startTime: String? = null,
    val endTime: String? = null,
    val location: String? = null,  // 미사용
)

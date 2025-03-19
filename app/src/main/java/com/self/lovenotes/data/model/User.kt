package com.self.lovenotes.data.model

import java.io.Serializable
import java.util.UUID

data class User(
    val uid: String,
    val inviteCode: String = UUID.randomUUID().toString().substring(0, 6),
    val subscribing: List<String> = emptyList(),
)

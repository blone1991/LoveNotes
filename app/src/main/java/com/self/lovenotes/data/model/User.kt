package com.self.lovenotes.data.model

import com.google.firebase.firestore.DocumentSnapshot
import java.util.UUID

data class User(
    val uid: String,
    val nickname: String = "",
    val invitationCode: String,
    val subscribing: List<String> = emptyList(),
) {
    constructor(document: DocumentSnapshot) : this(
        uid = document.getString("uid") ?: "",
        nickname = document.getString("nickname") ?: "",
        invitationCode = document.getString("invitationCode") ?: "",
        subscribing = document.get("subscribing") as? List<String> ?: emptyList(),
    )
}

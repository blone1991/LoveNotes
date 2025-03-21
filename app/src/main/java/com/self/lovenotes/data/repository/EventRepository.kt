package com.self.lovenotes.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore,
) {

    suspend fun updateEvent(modifiedEvent: Event) = withContext(Dispatchers.IO) {
        try {
            if (modifiedEvent.id.isEmpty()) {
                firestore.collection("events")
                    .add(modifiedEvent)
                    .addOnSuccessListener { docRef -> docRef.update("id", docRef.id) }
                    .await()
            } else {
                val snapshot = firestore.collection("events")
                    .document(modifiedEvent.id)
                    .get()
                    .await()

                snapshot.reference.update(modifiedEvent.toMap()).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()

            Log.e("EventRepository", "이벤트 ${if (modifiedEvent.id.isEmpty()) "추가" else "갱신"} 실패")
        }
    }

    suspend fun getEventsForDate(uid: String, date: String): List<Event> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("events")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", date)
                .get()
                .await()

            snapshot.map { Event(it) }.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EventRepository", "이벤트 정보 조회 실패")

            emptyList()
        }
    }
}
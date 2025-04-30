package com.self.lovenotes.data.remote.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.remote.model.Event
import com.self.lovenotes.data.util.utils.getMonthRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.YearMonth
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun updateEvent(modifiedEvent: Event) = withContext(Dispatchers.IO) {
        try {
            firestore.collection("events")
                .document(modifiedEvent.id)     // 로컬에서 ID를 만들기 때문에 항상 ID를 가진다.
                .set(modifiedEvent)
                .await()

        } catch (e: Exception) {
            e.printStackTrace()

            Log.e("EventRepository", "이벤트 ${if (modifiedEvent.id.isEmpty()) "추가" else "갱신"} 실패")
        }
    }

    suspend fun deleteEvent(event: Event) = withContext(Dispatchers.IO) {
        try {
            firestore.collection("events")
                .document(event.id)
                .delete()
                .await()
        } catch (e: Exception) {
            e.printStackTrace()

            Log.e("EventRepository", "이벤트 제거 실패")
        }
    }

    fun getEventsMontlyFlow(uids: List<String>, yearMonth: YearMonth) = callbackFlow<List<Event>> {
        val (startDate, endDate) = getMonthRange(yearMonth)
        val query = firestore.collection("events")
            .whereIn("uid", uids)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
        val listener = query.addSnapshotListener { value, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            value?.let { querySnapshot ->
                trySend(querySnapshot.map { Event(it) })
            } ?: run {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)
}
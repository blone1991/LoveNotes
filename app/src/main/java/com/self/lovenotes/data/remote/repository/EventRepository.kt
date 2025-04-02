package com.self.lovenotes.data.remote.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
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

    suspend fun getEventsForDate(uid: String, date: String): List<Event> =
        withContext(Dispatchers.IO) {
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


    suspend fun getEventsMontly(uid: String, date: String): List<Event> =
        withContext(Dispatchers.IO) {
            val (startDate, endDate) = getMonthRange(date)
            try {
                val snapshot = firestore.collection("events")
                    .whereEqualTo("uid", uid)
                    .whereGreaterThanOrEqualTo("date", startDate)
                    .whereLessThanOrEqualTo("date", endDate)
                    .get()
                    .await()

                snapshot.map { Event(it) }
            } catch (e: Exception) {
                Log.e("EventRepository", "Firestore 조회 실패", e)

                emptyList()
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

            value?.let {
                trySend(it.map { Event(it) })
            } ?: run {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)
}
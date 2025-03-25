package com.self.lovenotes.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun updateEvent(modifiedEvent: Event) = withContext(Dispatchers.IO) {
        try {
            firestore.collection("events")
                .document(modifiedEvent.id)
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

    // "2025-03-XX" -> ("2025-03-01", "2025-03-31") 변환 헬퍼 함수
    private fun getMonthRange(date: String): Pair<String, String> {
        val parts = date.split("-")
        val year = parts[0]
        val month = parts[1]
        val daysInMonth = java.time.YearMonth.of(year.toInt(), month.toInt()).lengthOfMonth()
        return ("$year-$month-01" to "$year-$month-$daysInMonth")
    }
}
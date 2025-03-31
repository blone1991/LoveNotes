package com.self.lovenotes.data.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.data.util.utils.getMonthRange
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DateMemoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun updateDateMemory(dateMemory: DateMemory) {
        if (dateMemory.id.isEmpty()) {      // 신규 추가
            firestore
                .collection("date_memories")
                .add(dateMemory.toMap())
                .addOnSuccessListener {
                    if (dateMemory.id.isEmpty()) {
                        it.update("id", it.id)
                        it.update("timeStamp", System.currentTimeMillis())
                    }
                }
                .await()
        } else {
            firestore
                .collection("date_memories")
                .document(dateMemory.id)
                .set(dateMemory.toMap())
                .await()
        }
    }

    suspend fun deleteDateMemory(dateMemory: DateMemory) {
        firestore
            .collection("date_memories")
            .document(dateMemory.id)
            .delete()
            .await()
    }

    suspend fun getMontlyDateMemeoriesForUid(uid: String, date: String): List<DateMemory> {
        val (startDate, endDate) = getMonthRange(date)
        val querySnapshot = firestore
            .collection("date_memories")
            .whereEqualTo("uid", uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .await()
        return querySnapshot.documents.map { DateMemory(it) }
    }

    suspend fun getMontlyDateMemeoriesForShareWith(uid: String, date: String): List<DateMemory> {
        val (startDate, endDate) = getMonthRange(date)
        val querySnapshot = firestore
            .collection("date_memories")
            .whereArrayContains("shareWith", uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .await()
        return querySnapshot.documents.map { DateMemory(it) }
    }

}
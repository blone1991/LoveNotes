package com.self.lovenotes.data.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.remote.model.DateMemory
import com.self.lovenotes.data.util.utils.getMonthRange
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import java.time.YearMonth
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

    suspend fun getMontlyDateMemeoriesForUid(uid: String, yearMonth: YearMonth): List<DateMemory> {
        val (startDate, endDate) = getMonthRange(yearMonth)
        val querySnapshot = firestore
            .collection("date_memories")
            .whereEqualTo("uid", uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .await()
        return querySnapshot.documents.map { DateMemory(it) }
    }

    suspend fun getMontlyDateMemeoriesForShareWith(uid: String, yearMonth: YearMonth): List<DateMemory> {
        val (startDate, endDate) = getMonthRange(yearMonth)
        val querySnapshot = firestore
            .collection("date_memories")
            .whereArrayContains("shareWith", uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .await()
        return querySnapshot.documents.map { DateMemory(it) }
    }

    fun getMyMemoriesFlow (uid: String, yearMonth: YearMonth) = callbackFlow<List<DateMemory>> {
        val (startDate, endDate) = getMonthRange(yearMonth)
        val query = firestore.collection("date_memories")
            .whereEqualTo("uid", uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
        val listener = query.addSnapshotListener { value, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            value?.documents?.let {
                trySend(it.map { DateMemory(it) })
            } ?: run {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)

    fun getSharedMemoriesFlow (uid: String, yearMonth: YearMonth) = callbackFlow<List<DateMemory>> {
        val (startDate, endDate) = getMonthRange(yearMonth)
        val query = firestore.collection("date_memories")
            .whereArrayContains("shareWith", uid)
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
        val listener = query.addSnapshotListener { value, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }

            value?.documents?.let {
                trySend(it.map { DateMemory(it) })
            } ?: run {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }.flowOn(Dispatchers.IO)
}
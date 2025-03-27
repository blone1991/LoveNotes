package com.self.lovenotes.data.remote.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.remote.model.DateMemory
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class DateMemoryRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    suspend fun updateDateMemory(dateMemory: DateMemory) {
        firestore
            .collection("date_memories")
            .add(dateMemory)
            .addOnSuccessListener {
                it.update("id", it.id)
            }
            .await()
    }

    suspend fun deleteDateMemory(dateMemory: DateMemory) {
        firestore
            .collection("date_memories")
            .document(dateMemory.id)
            .delete()
            .await()
    }

    suspend fun getDateMemeoriesForUid(uid: String): List<DateMemory> {
        val querySnapshot = firestore
            .collection("date_memories")
            .whereEqualTo("uid", uid)
            .get()
            .await()

        return querySnapshot.documents.map { DateMemory(it) }
    }

    suspend fun getDateMemeoriesForShareWith(uid: String): List<DateMemory> {
        val querySnapshot = firestore
            .collection("date_memories")
            .whereEqualTo("shareWith", uid)
            .get()
            .await()

        return querySnapshot.documents.map { DateMemory(it) }
    }


}
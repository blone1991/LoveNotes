package com.self.lovenotes.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.model.User
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {

    suspend fun login(): String? = withContext(Dispatchers.IO) {
        auth.currentUser?.uid
            ?: auth.signInAnonymously().await().user?.uid
            ?: return@withContext null
    }

    suspend fun getUser(): User? = withContext(Dispatchers.IO) {
        try {
            val uid = login() ?: return@withContext null

            val userSnapshot = firestore.collection("users")
                .document(uid)
                .get().await()

            val user = if (userSnapshot.exists()) {    // 있으면 기존 정보 반환
                User(
                    userSnapshot["uid"] as String,
                    userSnapshot["inviteCode"] as String,
                    userSnapshot["subscribing"] as List<String>
                )
            } else {                        // 없으면 생성
                val newUser = User(uid = uid)
                userSnapshot.reference.set(newUser).await()
                newUser
            }

            return@withContext user
        } catch (e: Exception) {
            Log.e("UserRepository", "사용자 정보 조회/생성 실패", e)
            null
        }
    }

    suspend fun deleteUser() = withContext(Dispatchers.IO) {
        try {
            val uid = login() ?: return@withContext null

            firestore.collection("users")
                .document(uid)
                .delete()
                .await()

            val subscribersQuery = firestore.collection("users")
                .whereArrayContains("subscribed", uid)
                .get().await()

            firestore.runTransaction { transaction ->
                for (subscriberDoc in subscribersQuery.documents) {
                    val subscriber = subscriberDoc.toObject(User::class.java)!!
                    val updatedSubscribed = subscriber.subscribing.filter { it != uid }

                    // 업데이트된 subscribed 목록으로 문서 업데이트
                    transaction.update(
                        subscriberDoc.reference,
                        "subscribed",
                        updatedSubscribed
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("UserRepository", "사용자 정보 제거 실패", e)
        }
    }

    suspend fun addSubscribing(inviteCode: String) = withContext(Dispatchers.IO) {
        try {
            val my = getUser() ?: return@withContext

            val snapshot = firestore.collection("users")
                .whereEqualTo("inviteCode", inviteCode)
                .get().await()

            val invitor = snapshot.firstOrNull()?.get("uid") ?: return@withContext

            firestore.collection("users")
                .document(my.uid)
                .update("subscribing", my.subscribing + invitor)
                .await()
        } catch (e: Exception) {
            Log.e("UserRepository", "구독자 추가 실패", e)
        }
    }

    suspend fun clearSubscribing() = withContext(Dispatchers.IO) {
        try {
            val uid = login() ?: return@withContext

            firestore.collection("users")
                .document(uid)
                .update("subscribing", emptyList<String>())
                .await()
        } catch (e: Exception) {
            Log.e("UserRepository", "구독자 추가 실패", e)
        }
    }
}
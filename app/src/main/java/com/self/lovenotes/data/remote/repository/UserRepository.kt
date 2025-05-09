package com.self.lovenotes.data.remote.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.remote.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) {
    fun login(): String? = auth.currentUser?.uid

    var _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    var _userInfos = MutableStateFlow<List<User>>(listOf())
    val userInfos = _userInfos.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            firebaseAuth.currentUser?.let {
                listenToCurrentUser(it.uid)
            } ?: run {
                _currentUser.value = null
            }
        }
    }

    private fun listenToCurrentUser(uid: String) {
        firestore.collection("users")
            .document(uid)
            .addSnapshotListener { doc, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Error fetching user: $error")
                    return@addSnapshotListener
                }

                val user = doc?.let { User(doc) }
                _currentUser.value = user
                user?.subscribing?.let { fetchSubscribingUsers(it) } ?: run {
                    _userInfos.value = emptyList()
                }
            }
    }

    private fun fetchSubscribingUsers(subscribedUids: List<String>) {
        var usersList = listOf(_currentUser.value!!)

        if (subscribedUids.isEmpty()) {
            _userInfos.value = usersList
            return
        }

        firestore.collection("users")
            .whereIn("uid", subscribedUids)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("FirebaseRepository", "Error fetching subscribed users: $error")
                    return@addSnapshotListener
                }
                _userInfos.value = usersList + (snapshot?.documents?.mapNotNull { User(it) } ?: emptyList())
            }
    }


    suspend fun getUser(uid: String? = null): User? = withContext(Dispatchers.IO) {
        try {
            val userId = uid ?: login() ?: return@withContext null

            val userSnapshot = firestore.collection("users")
                .document(userId)
                .get().await()

            val user = if (userSnapshot.exists()) {    // 있으면 기존 정보 반환
                User(userSnapshot)
            } else {                        // 없으면 생성
                val users = firestore.collection("users")
                    .get().await()

                val exists = users.mapNotNull { it.data.get("uid") }.toList()

                // 초대코드가 겹치지 않도록.
                var genUid = ""
                while (true) {
                    genUid = UUID.randomUUID().toString().substring(0, 6)
                    if (exists.firstOrNull { it == genUid } == null) {
                        break
                    }
                }

                val newUser = User(uid = userId, invitationCode = genUid)
                userSnapshot.reference.set(newUser)
                    .addOnSuccessListener {
                        userSnapshot.reference.update(
                            "uid",
                            userSnapshot.reference.id
                        )
                    }
                    .await()
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

    suspend fun updateUser(user: User) = withContext(Dispatchers.IO) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

    // 초대 코드로 사용자 검색 (예: 초대 기능 지원)
    suspend fun findUserByInvitationCode(code: String): User? = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("invitationCode", code)
                .get()
                .await()

            snapshot.documents.firstOrNull()?.let { User(it) }
        } catch (e: Exception) {
            Log.e("UserRepository", "초대 코드로 사용자 검색 실패", e)
            null
        }
    }
}
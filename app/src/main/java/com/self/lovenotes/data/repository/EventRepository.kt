package com.self.lovenotes.data.repository

import android.util.Log
import android.util.Printer
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.self.lovenotes.data.model.Event
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class EventRepository @Inject constructor(
    private val userRepository: UserRepository,
    private val firestore: FirebaseFirestore,
) {
    suspend fun addEvent(title: String, date: String) = withContext(Dispatchers.IO) {
        try {
            val uid = userRepository.login() ?: return@withContext
            firestore.collection("events")
                .add(Event(uid = uid, title = title, date = date, allDay = true))
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EventRepository", "이벤트 추가 실패")
        }
    }

    suspend fun getEvents(uid: String): List<Event> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("events")
                .whereEqualTo("uid", uid)
                .get()
                .await()

            snapshot.map { snap ->
                Event(
                    uid = snap["uid"] as String,
                    title = snap["title"] as String,
                    date = snap["date"] as String,
                    allDay = snap["allDay"] as Boolean,
                    startTime = snap["startTime"] as String?,
                    endTime = snap["endTime"] as String?,
                    location = snap["location"] as String?
                )
            }.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EventRepository", "이벤트 정보 조회 실패")
            emptyList<Event>()
        }
    }

    suspend fun getEvents(uid: String, date: String): List<Event> = withContext(Dispatchers.IO) {
        try {
            val snapshot = firestore.collection("events")
                .whereEqualTo("uid", uid)
                .whereEqualTo("date", date)
                .get()
                .await()

            snapshot.map { snap ->
                Event(
                    uid = snap["uid"] as String,
                    title = snap["title"] as String,
                    date = snap["date"] as String,
                    allDay = snap["allDay"] as Boolean,
                    startTime = snap["startTime"] as String?,
                    endTime = snap["endTime"] as String?,
                    location = snap["location"] as String?
                )
            }.toList()
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EventRepository", "이벤트 정보 조회 실패")
            emptyList<Event>()
        }
    }

    suspend fun fetchAllEvents(): List<Event> = withContext(Dispatchers.IO) {
        try {
            val my = userRepository.getUser() ?: return@withContext emptyList()

            // 1. 내가 등록한 이벤트
            var events = getEvents(my.uid).toMutableList()

            // 2. 내가 구독한 이벤트
            my.subscribing.forEach {
                events += getEvents(it)
            }

            return@withContext events
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EventRepository", "이벤트 정보 조회 실패")
            emptyList<Event>()
        }
    }

    suspend fun fetchEvents(date: String): List<Event> = withContext(Dispatchers.IO) {
        try {
            val my = userRepository.getUser() ?: return@withContext emptyList()

            // 1. 내가 등록한 이벤트
            var events = getEvents(my.uid, date).toMutableList()

            // 2. 내가 구독한 이벤트
            my.subscribing.forEach {
                events += getEvents(it, date)
            }

            return@withContext events
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("EventRepository", "이벤트 정보 조회 실패")
            emptyList<Event>()
        }
    }
}
package com.self.lovenotes.workers

import android.annotation.SuppressLint
import android.content.Context
import androidx.room.Room
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.self.lovenotes.data.db.AppDatabase
import com.self.lovenotes.data.local.entity.PathEntity
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import java.util.concurrent.TimeUnit


class LocationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)
    private val db = Room.databaseBuilder(context, AppDatabase::class.java, "love_notes_db").build()
    private val prefs = context.getSharedPreferences("LoveNotes", Context.MODE_PRIVATE)


    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        return try {
            val location = fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()

            if (location != null) {
                val sessionId = getOrCreateSessionId()
                val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

                db.pathDao().insert(
                    PathEntity(
                        date = date,
                        timestamp = location.time,
                        latlng = "${location.latitude},${location.longitude}",
                        sessionId = sessionId,
                    )
                )
                Result.success()
            } else {
                Result.retry() // 위성 못 잡았을 경우 재시도
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun getOrCreateSessionId(): String {
        return prefs.getString("CURRENT_SESSION_ID", null) ?: UUID.randomUUID().toString().also {
            prefs.edit().putString("CURRENT_SESSION_ID", it).apply()
        }
    }
}

fun startLocationTrackingWork(context: Context) {
    val workManager = WorkManager.getInstance(context)

    // 즉시 1회 실행
    val oneTimeWork = OneTimeWorkRequestBuilder<LocationWorker>().build()
    workManager.enqueue(oneTimeWork)

    // 반복 실행
    val periodicWork = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES).build()
    workManager.enqueueUniquePeriodicWork(
        "LocationTracking",
        ExistingPeriodicWorkPolicy.KEEP,
        periodicWork
    )
}

fun stopLocationTrackingWork(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork("LocationTracking")
}

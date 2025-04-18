package com.self.lovenotes.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.self.lovenotes.data.local.dao.PathDao
import com.self.lovenotes.data.local.entity.PathEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Timer
import java.util.TimerTask
import javax.inject.Inject

@AndroidEntryPoint
class TrackingService : Service() {
    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient
    @Inject
    lateinit var pathDao: PathDao
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private val scope = CoroutineScope(Dispatchers.IO)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            val sessionId: String? = sharedPreferences.getString("CURRENT_SESSION_ID", null)
            if (sessionId.isNullOrEmpty()) {
                // 세션 ID가 없으면 위치 업데이트 중단
                fusedLocationClient.removeLocationUpdates(this)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                } else {
                    @Suppress("DEPRECATION")
                    stopForeground(true)
                }

                stopSelf()
                Log.d("LocationCallback", "세션 종료됨. 위치 업데이트 중단.")
                return
            } else {
                Log.d("LocationCallback", "위치 정보 업데이트 중.")
                result.locations.forEach { location ->
                    scope.launch {
                        val date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
                        pathDao.insert(
                            PathEntity(
                                date = date,
                                timestamp = location.time,
                                latlng = "${location.latitude},${location.longitude}",
                                sessionId = sessionId
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        val notification = NotificationCompat.Builder(this, "tracking_channel")
            .setContentTitle("Tracking Date")
            .setContentText("Recording your path...")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                1,
                notification,
                android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(1, notification)
        }

        Log.d("TrackingService", "onStartCommand: start")
        registerLocationLoop();

        return START_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationLoop (): Unit {
        Log.d("TrackingService", "onStartCommand: start")
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
            .build()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun stopService(name: Intent?): Boolean {
        return super.stopService(name)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)

        // 서비스 재시작 (SESSION_ID가 초기화되지 않았으면)
        sharedPreferences.getString("CURRENT_SESSION_ID", null) ?.let {
            val restartIntent = Intent(this, TrackingService::class.java)
            startService(restartIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "tracking_channel",
            "Tracking Service",
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}
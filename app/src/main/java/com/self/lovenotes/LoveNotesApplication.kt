package com.self.lovenotes

import android.app.Application
import android.content.IntentFilter
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LoveNotesApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
            Log.d("FirebaseInit", "Firebase initialized successfully")
        } else {
            Log.d("FirebaseInit", "Firebase already initialized")
        }

        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(false)
        }
    }
}
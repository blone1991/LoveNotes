package com.self.lovenotes.di

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(@ApplicationContext context: Context): FirebaseAuth {
        if (FirebaseApp.getApps(context).isEmpty())
            FirebaseApp.initializeApp(context)
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirestore(@ApplicationContext context: Context): FirebaseFirestore {
        if (FirebaseApp.getApps(context).isEmpty())
            FirebaseApp.initializeApp(context)
        return FirebaseFirestore.getInstance()
    }
}
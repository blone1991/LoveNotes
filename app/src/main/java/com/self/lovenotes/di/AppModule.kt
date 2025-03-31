package com.self.lovenotes.di

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.db.AppDatabase
import com.self.lovenotes.data.remote.repository.AiGeneratorRepository
import com.self.lovenotes.data.remote.repository.EventRepository
import com.self.lovenotes.data.remote.repository.UserRepository
import com.self.lovenotes.domain.usecase.CalendarUsecase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): UserRepository {
        return UserRepository(auth, firestore)
    }

    @Provides
    @Singleton
    fun provideEventRepository(
        firestore: FirebaseFirestore,
    ): EventRepository {
        return EventRepository(firestore)
    }

    @Provides
    @Singleton
    fun provideCanlendarUsecase(userRepository: UserRepository, eventRepository: EventRepository) =
        CalendarUsecase(userRepository, eventRepository)

    @Provides
    @Singleton
    fun provideAIGeneratorRepository() = AiGeneratorRepository()

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "love_notes_db").build()
    }

    @Provides
    @Singleton
    fun providePathDao(database: AppDatabase) = database.pathDao()

    @Provides
    @Singleton
    fun provideFusedLocationClient(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    @Singleton
    fun provideSharedPreferrence(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("LoveNotes", 0)
}
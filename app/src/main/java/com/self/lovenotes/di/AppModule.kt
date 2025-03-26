package com.self.lovenotes.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.repository.AiGeneratorRepository
import com.self.lovenotes.data.repository.EventRepository
import com.self.lovenotes.data.repository.UserRepository
import com.self.lovenotes.data.util.NetworkChecker
import com.self.lovenotes.domain.CalendarUsecase
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
    fun provideNetworkChecker(@ApplicationContext context: Context) = NetworkChecker(context)
}
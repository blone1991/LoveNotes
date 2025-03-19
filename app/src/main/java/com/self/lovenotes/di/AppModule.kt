package com.self.lovenotes.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.self.lovenotes.data.repository.EventRepository
import com.self.lovenotes.data.repository.UserRepository
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
        userRepository: UserRepository,
        firestore: FirebaseFirestore,
    ): EventRepository {
        return EventRepository(userRepository, firestore)
    }
}
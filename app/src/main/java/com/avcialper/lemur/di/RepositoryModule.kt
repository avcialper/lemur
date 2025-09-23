package com.avcialper.lemur.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.avcialper.lemur.data.repository.auth.AuthRepository
import com.avcialper.lemur.data.repository.auth.AuthRepositoryImpl
import com.avcialper.lemur.data.repository.datastore.DataStoreRepository
import com.avcialper.lemur.data.repository.datastore.DataStoreRepositoryImpl
import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.repository.storage.StorageRepositoryImpl
import com.avcialper.lemur.data.repository.storage.role.RoleRepository
import com.avcialper.lemur.data.repository.storage.role.RoleRepositoryImpl
import com.avcialper.lemur.data.repository.storage.room.RoomRepository
import com.avcialper.lemur.data.repository.storage.room.RoomRepositoryImpl
import com.avcialper.lemur.data.repository.storage.task.TaskRepository
import com.avcialper.lemur.data.repository.storage.task.TaskRepositoryImpl
import com.avcialper.lemur.data.repository.storage.team.TeamRepository
import com.avcialper.lemur.data.repository.storage.team.TeamRepositoryImpl
import com.avcialper.lemur.data.repository.storage.user.UserRepository
import com.avcialper.lemur.data.repository.storage.user.UserRepositoryImpl
import com.avcialper.lemur.util.constant.Constants.DATASTORE_NAME
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
object RepositoryModule {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATASTORE_NAME)

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository = AuthRepositoryImpl(auth)

    @Provides
    @Singleton
    fun provideStorageRepository(api: StorageApi): StorageRepository =
        StorageRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore): UserRepository =
        UserRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providesTaskRepository(firestore: FirebaseFirestore): TaskRepository =
        TaskRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providesTeamRepository(firestore: FirebaseFirestore): TeamRepository =
        TeamRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providesRoomRepository(firestore: FirebaseFirestore): RoomRepository =
        RoomRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun providesRoleRepository(firestore: FirebaseFirestore): RoleRepository =
        RoleRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository =
        DataStoreRepositoryImpl(context.dataStore)
}
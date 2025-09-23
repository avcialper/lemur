package com.avcialper.lemur.data.repository.storage.user

import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun createUser(userProfile: UserProfile): Flow<Resource<Boolean>>
    suspend fun getUser(id: String): Flow<Resource<UserProfile>>
    suspend fun updateUser(userProfile: UserProfile): Flow<Resource<Boolean>>
}
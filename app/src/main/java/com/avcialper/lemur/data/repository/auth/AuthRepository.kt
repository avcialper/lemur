package com.avcialper.lemur.data.repository.auth

import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?

    suspend fun signup(
        email: String,
        password: String,
    ): Flow<Resource<FirebaseUser>>

    suspend fun login(email: String, password: String): Flow<Resource<FirebaseUser>>

    suspend fun logout(): Flow<Resource<Boolean>>

    suspend fun forgotPassword(email: String): Flow<Resource<Boolean>>

    suspend fun isLoggedIn(): Flow<Boolean>
}
package com.avcialper.lemur.data.repository.auth

import com.avcialper.lemur.data.model.ImgBBData
import com.avcialper.lemur.util.constants.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?

    suspend fun signup(
        username: String,
        email: String,
        password: String,
        imgBB: ImgBBData?
    ): Flow<Resource<FirebaseUser>>

    suspend fun login(email: String, password: String): Flow<Resource<FirebaseUser>>

    suspend fun logout(): Flow<Resource<Boolean>>

    suspend fun forgotPassword(email: String): Flow<Resource<Boolean>>

    suspend fun isLoggedIn(): Flow<Boolean>
}
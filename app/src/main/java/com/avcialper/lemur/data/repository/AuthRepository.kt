package com.avcialper.lemur.data.repository

import android.net.Uri
import com.avcialper.lemur.util.constants.Resource
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?

    suspend fun signup(
        username: String,
        email: String,
        password: String,
        imageUri: Uri?
    ): Resource<FirebaseUser>

    suspend fun login(email: String, password: String): Resource<FirebaseUser>

    suspend fun logout(): Resource<Boolean>

    suspend fun forgotPassword(email: String): Resource<Boolean>

    suspend fun isLoggedIn(): Boolean
}
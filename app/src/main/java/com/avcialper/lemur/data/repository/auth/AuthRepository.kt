package com.avcialper.lemur.data.repository.auth

import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: FirebaseUser?
    fun reload(): Flow<FirebaseUser?>
    fun signup(email: String, password: String): Flow<Resource<FirebaseUser>>
    fun login(email: String, password: String): Flow<Resource<FirebaseUser>>
    fun logout(): Flow<Resource<Boolean>>
    fun forgotPassword(email: String): Flow<Resource<Boolean>>
    fun isLoggedIn(): Flow<Boolean>
    fun sendEmailVerification(): Flow<Resource<Boolean>>
    fun updatePassword(password: String, newPassword: String): Flow<Resource<Boolean>>
    fun updateEmail(email: String, password: String): Flow<Resource<Boolean>>
}
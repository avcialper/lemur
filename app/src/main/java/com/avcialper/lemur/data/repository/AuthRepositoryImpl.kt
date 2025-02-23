package com.avcialper.lemur.data.repository

import android.net.Uri
import com.avcialper.lemur.util.constants.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signup(
        username: String,
        email: String,
        password: String,
        imageUri: Uri?
    ): Resource<FirebaseUser> {
        // TODO: Implement signup logic
        return Resource.Loading()
    }

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            Resource.Success(user)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    override suspend fun logout(): Resource<Boolean> {
        // TODO Implement logout logic
        return Resource.Success(true)
    }

    override suspend fun forgotPassword(email: String): Resource<Boolean> {
        // TODO Implement forgot password logic
        return Resource.Loading()
    }

    override suspend fun isLoggedIn(): Boolean = auth.currentUser != null
}
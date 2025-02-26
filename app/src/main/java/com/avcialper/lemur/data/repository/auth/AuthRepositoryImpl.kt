package com.avcialper.lemur.data.repository.auth

import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override suspend fun signup(email: String, password: String): Flow<Resource<FirebaseUser>> =
        flow {
            emit(Resource.Loading())
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                emit(Resource.Success(user))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e))
            }
        }

    override suspend fun login(email: String, password: String): Flow<Resource<FirebaseUser>> =
        flow {
            emit(Resource.Loading())
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val user = result.user
                emit(Resource.Success(user))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e))
            }
        }

    override suspend fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        auth.signOut()
        emit(Resource.Success(true))
    }

    override suspend fun forgotPassword(email: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }

    override suspend fun isLoggedIn(): Flow<Boolean> = flow {
        val isLogged = auth.currentUser != null
        emit(isLogged)
    }
}
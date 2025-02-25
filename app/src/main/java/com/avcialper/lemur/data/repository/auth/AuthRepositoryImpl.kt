package com.avcialper.lemur.data.repository.auth

import com.avcialper.lemur.data.model.ImgBBData
import com.avcialper.lemur.util.constants.Resource
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

    override suspend fun signup(
        username: String,
        email: String,
        password: String,
        imgBB: ImgBBData?
    ): Flow<Resource<FirebaseUser>> = flow {
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
        // TODO Implement logout logic
    }

    override suspend fun forgotPassword(email: String): Flow<Resource<Boolean>> = flow {
        // TODO Implement forgot password logic
    }

    override suspend fun isLoggedIn(): Flow<Boolean> = flow {
        val isLogged = auth.currentUser != null
        emit(isLogged)
    }
}
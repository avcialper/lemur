package com.avcialper.lemur.data.repository.auth

import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override fun reload(): Flow<FirebaseUser?> = flow {
        try {
            auth.currentUser?.reload()?.await()
            emit(auth.currentUser)
        } catch (e: Exception) {
            emit(auth.currentUser)
        }
    }

    override fun signup(email: String, password: String): Flow<Resource<FirebaseUser>> =
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

    override fun login(email: String, password: String): Flow<Resource<FirebaseUser>> =
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

    override fun logout(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        auth.signOut()
        emit(Resource.Success(true))
    }

    override fun forgotPassword(email: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.sendPasswordResetEmail(email).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }

    override fun isLoggedIn(): Flow<Boolean> = flow {
        val isLogged = auth.currentUser != null
        emit(isLogged)
    }

    override fun sendEmailVerification(): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.currentUser!!.sendEmailVerification().await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }

    override fun updatePassword(password: String, newPassword: String): Flow<Resource<Boolean>> =
        flow {
            emit(Resource.Loading())
            try {
                auth.currentUser?.let { user ->
                    val credential = EmailAuthProvider.getCredential(user.email!!, password)
                    user.reauthenticate(credential).await()
                    user.updatePassword(newPassword).await()
                }
                emit(Resource.Success(true))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error(e))
            }
        }

    override fun updateEmail(email: String, password: String): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            auth.currentUser?.let {
                val credential = EmailAuthProvider.getCredential(UserManager.user!!.email, password)
                it.reauthenticate(credential).await()
                it.verifyBeforeUpdateEmail(email).await()
            }
            emit(Resource.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }
}
package com.avcialper.lemur.data.repository.auth

import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.repository.flowWithResource
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
            emit(null)
        }
    }

    override fun signup(email: String, password: String): Flow<Resource<FirebaseUser>> =
        flowWithResource {
            auth.createUserWithEmailAndPassword(email, password).await().user!!
        }

    override fun login(email: String, password: String): Flow<Resource<FirebaseUser>> =
        flowWithResource {
            auth.signInWithEmailAndPassword(email, password).await().user!!
        }

    override fun logout(): Flow<Resource<Boolean>> = flowWithResource {
        auth.signOut()
        true
    }

    override fun forgotPassword(email: String): Flow<Resource<Boolean>> = flowWithResource {
        auth.sendPasswordResetEmail(email).await()
        true
    }

    override fun isLoggedIn(): Flow<Boolean> = flow {
        val isLogged = auth.currentUser != null
        emit(isLogged)
    }

    override fun sendEmailVerification(): Flow<Resource<Boolean>> = flowWithResource {
        auth.currentUser!!.sendEmailVerification().await()
        true
    }

    override fun updatePassword(password: String, newPassword: String): Flow<Resource<Boolean>> =
        flowWithResource {
            auth.currentUser?.let { user ->
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                user.reauthenticate(credential).await()
                user.updatePassword(newPassword).await()
            }
            true
        }

    override fun updateEmail(email: String, password: String): Flow<Resource<Boolean>> =
        flowWithResource {
            auth.currentUser?.let {
                val credential = EmailAuthProvider.getCredential(UserManager.user!!.email, password)
                it.reauthenticate(credential).await()
                it.verifyBeforeUpdateEmail(email).await()
            }
            true
        }
}
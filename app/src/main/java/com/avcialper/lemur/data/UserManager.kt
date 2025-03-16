package com.avcialper.lemur.data

import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.IconLabelData
import com.avcialper.lemur.data.model.User
import com.google.firebase.auth.FirebaseUser

object UserManager {

    var user: User? = null

    fun updateUser(
        firebaseUser: FirebaseUser,
        username: String,
        imageUrl: String?,
    ) {
        val newUser = User(
            firebaseUser = firebaseUser,
            id = firebaseUser.uid,
            username = username,
            imageUrl = imageUrl,
            email = firebaseUser.email!!,
        )
        user = newUser
    }

    fun logout() {
        user = null
    }

    fun emailIconAndLabelIds(): IconLabelData {
        val isVerified = user?.firebaseUser?.isEmailVerified

        return when (isVerified) {
            true -> IconLabelData(R.drawable.ic_email_verified, R.string.email_verified)
            else -> IconLabelData(R.drawable.ic_email, R.string.email_verify)
        }
    }

}

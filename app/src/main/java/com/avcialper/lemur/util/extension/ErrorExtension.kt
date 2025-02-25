package com.avcialper.lemur.util.extension

import android.content.Context
import androidx.core.content.ContextCompat
import com.avcialper.lemur.R
import com.google.firebase.auth.FirebaseAuthException

fun Context.exceptionConverter(e: Exception): String {
    return if (e is FirebaseAuthException) {
        val messageId = when (e.errorCode) {
            "ERROR_EMAIL_ALREADY_IN_USE" -> R.string.error_email_already_exists
            "ERROR_TOO_MANY_REQUESTS" -> R.string.error_to_many_request
            "ERROR_INVALID_CREDENTIAL" -> R.string.error_invalid_credential
            "ERROR_USER_NOT_FOUND" -> R.string.error_user_not_found
            "ERROR_USER_DISABLED" -> R.string.error_login_user_disabled
            "ERROR_WEAK_PASSWORD" -> R.string.error_login_password_is_weak
            else -> R.string.error_unknown
        }
        ContextCompat.getString(this, messageId)

    } else {
        ContextCompat.getString(this, R.string.error_unknown)
    }
}
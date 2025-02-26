package com.avcialper.lemur.data.state

import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.auth.FirebaseUser

data class LoginState(
    val email: String = "",
    val password: String = "",
    val resource: Resource<FirebaseUser>? = null
)
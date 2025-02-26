package com.avcialper.lemur.data.state

import com.avcialper.lemur.util.constant.Resource

data class LoginState(
    val email: String = "",
    val password: String = "",
    val resource: Resource<Boolean>? = null
)
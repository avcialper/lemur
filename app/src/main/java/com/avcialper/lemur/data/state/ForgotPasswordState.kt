package com.avcialper.lemur.data.state

import com.avcialper.lemur.util.constant.Resource

data class ForgotPasswordState(
    val email: String = "",
    val resource: Resource<Boolean>? = null
)
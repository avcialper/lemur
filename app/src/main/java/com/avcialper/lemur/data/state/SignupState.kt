package com.avcialper.lemur.data.state

import android.net.Uri
import com.avcialper.lemur.data.model.ImgBBData
import com.avcialper.lemur.util.constant.Resource

data class SignupState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val imageUri: Uri? = null,
    val imgBB: ImgBBData? = null,
    val resource: Resource<Boolean>? = null
)
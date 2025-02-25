package com.avcialper.lemur.data.state

import android.net.Uri
import com.avcialper.lemur.data.model.ImgBBData
import com.avcialper.lemur.util.constant.Resource

data class SignupState(
    var username: String = "",
    var email: String = "",
    var password: String = "",
    var confirmPassword: String = "",
    var imageUri: Uri? = null,
    var imgBB: ImgBBData? = null,
    val resource: Resource<Boolean>? = null
)
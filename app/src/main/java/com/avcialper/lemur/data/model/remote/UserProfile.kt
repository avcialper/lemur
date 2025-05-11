package com.avcialper.lemur.data.model.remote

import com.avcialper.lemur.util.constant.Constants

data class UserProfile(
    val id: String,
    val username: String,
    val about: String?,
    val imageUrl: String?,
) {
    constructor() : this("", "", "", null)

    fun toMap(): HashMap<String, Any?> = hashMapOf(
        Constants.USER_ID to id,
        Constants.USERNAME to username,
        Constants.ABOUT to about,
        Constants.IMAGE_URL to imageUrl,
    )
}

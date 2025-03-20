package com.avcialper.lemur.data.model.remote

data class UserProfile(
    val id: String,
    val username: String,
    val about: String?,
    val imageUrl: String?,
) {
    constructor() : this("", "", "", null)
}

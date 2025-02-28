package com.avcialper.lemur.data.model

data class UserProfile(
    val id: String,
    val username: String,
    val imageUrl: String?,
) {
    constructor() : this("", "", null)
}

package com.avcialper.lemur.data.model

data class UserProfile(
    val id: String,
    val username: String,
    val imageUrl: String?,
    val imageDeleteUrl: String?,
) {
    constructor() : this("", "", null, null)
}

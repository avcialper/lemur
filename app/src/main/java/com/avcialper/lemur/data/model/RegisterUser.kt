package com.avcialper.lemur.data.model

data class RegisterUser(
    val id: String,
    val username: String,
    val imageUrl: String?,
    val imageDeleteUrl: String?,
)

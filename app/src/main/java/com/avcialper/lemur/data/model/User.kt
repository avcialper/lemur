package com.avcialper.lemur.data.model

import com.google.firebase.auth.FirebaseUser

data class User(
    val firebaseUser: FirebaseUser,
    val id: String,
    val username: String,
    val imageUrl: String?,
    val email: String,
)
package com.avcialper.lemur.data.model

import com.google.firebase.auth.FirebaseUser

data class User(
    var firebaseUser: FirebaseUser,
    var id: String,
    var username: String,
    var about: String,
    var imageUrl: String,
    var email: String,
    var teams: List<String>
)
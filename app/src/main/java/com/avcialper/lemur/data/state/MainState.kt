package com.avcialper.lemur.data.state

import com.google.firebase.auth.FirebaseUser

data class MainState(
    val user: FirebaseUser? = null,
    val isCurrentUserChecked: Boolean = false,
    val isThemeChecked: Boolean = false
)
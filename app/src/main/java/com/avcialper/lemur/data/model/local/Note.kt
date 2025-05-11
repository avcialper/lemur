package com.avcialper.lemur.data.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Note(
    val note: String,
    val ownerID: String,
    val date: String,
    val time: String
) : Parcelable
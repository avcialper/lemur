package com.avcialper.lemur.data.model.remote

import com.google.gson.annotations.SerializedName

data class ImgBBResponse(
    @SerializedName("data")
    val data: ImgBBData,
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("status")
    val status: Int
)

data class ImgBBData(
    @SerializedName("id") val id: String,
    @SerializedName("url") val url: String,
)
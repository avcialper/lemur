package com.avcialper.lemur.data.model.local

data class Role(
    val code: String,
    val name: String
) {
    constructor() : this("", "")
}
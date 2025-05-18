package com.avcialper.lemur.data.model.local

data class Member(
    val id: String,
    val roleCode: String
) {
    constructor() : this("", "")
}
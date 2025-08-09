package com.avcialper.lemur.data.model.local


data class Member(
    val id: String,
    val roleCodes: List<String>,
) {
    constructor() : this("", emptyList())

    fun toMemberCard(name: String, roleNames: List<String>, imageUrl: String?) =
        MemberCard(id, name, roleCodes, roleNames, imageUrl)
}
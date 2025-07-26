package com.avcialper.lemur.data.model.local


data class Member(
    val id: String,
    val roleCode: String,
    var roleName: String
) {
    constructor() : this("", "", "")

    fun toMemberCard(name: String, imageUrl: String?) =
        MemberCard(id, name, roleCode, roleName, imageUrl)
}
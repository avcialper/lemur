package com.avcialper.lemur.data.model.local

data class MemberCard(
    val id: String,
    val name: String,
    val roleCode: String,
    val role: String,
    val imageUrl: String?
) {

    fun toMember(): Member = Member(id, roleCode)

}

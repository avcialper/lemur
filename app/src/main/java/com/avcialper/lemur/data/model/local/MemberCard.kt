package com.avcialper.lemur.data.model.local

data class MemberCard(
    val id: String,
    val name: String,
    val roleCodes: List<String>,
    val roleNames: List<String>,
    val imageUrl: String?
) {

    fun toMember(): Member = Member(id, roleCodes)

    fun toSelectableMemberCard() = SelectableMemberCard(imageUrl, id, name)
}

package com.avcialper.lemur.data.model.local


data class Member(
    val id: String,
    var roleCodes: List<String>,
) {
    constructor() : this("", emptyList())

    fun toMemberCard(
        name: String,
        roleNames: List<String>,
        imageUrl: String?,
        permissions: List<String>
    ) =
        MemberCard(id, name, roleCodes, roleNames, imageUrl, permissions)

    fun toSelectableMemberCard(name: String, imageUrl: String?) =
        SelectableMemberCard(imageUrl, id, name)
}
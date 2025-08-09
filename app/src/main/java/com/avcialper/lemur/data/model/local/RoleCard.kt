package com.avcialper.lemur.data.model.local

data class RoleCard(
    val code: String,
    val name: String,
    val permissions: List<String>,
    var isSelected: Boolean = false
) {

    fun toRole(): Role = Role(code, name, permissions)

}

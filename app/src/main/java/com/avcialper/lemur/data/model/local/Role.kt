package com.avcialper.lemur.data.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
    val code: String,
    val name: String,
    val permissions: List<String>
) : Parcelable {
    constructor() : this("", "", emptyList())

    fun toRoleCard(): RoleCard = RoleCard(code, name, permissions)
}
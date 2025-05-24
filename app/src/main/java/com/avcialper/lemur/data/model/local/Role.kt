package com.avcialper.lemur.data.model.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Role(
    val code: String,
    val name: String
) : Parcelable {
    constructor() : this("", "")

    fun toRoleCard(): RoleCard = RoleCard(code, name)
}
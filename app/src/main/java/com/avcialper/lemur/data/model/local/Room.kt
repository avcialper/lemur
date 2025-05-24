package com.avcialper.lemur.data.model.local

import com.avcialper.lemur.util.constant.Constants

data class Room(
    val id: String,
    val teamId: String,
    val name: String,
    val description: String?,
    val accessibleRoles: List<String>,
) {
    constructor() : this("","", "", "", emptyList())

    fun toMap(): Map<String, Any?> = hashMapOf(
        Constants.ROOM_ID to id,
        Constants.ROOM_TEAM_ID to teamId,
        Constants.ROOM_NAME to name,
        Constants.ROOM_DESCRIPTION to description,
        Constants.ROOM_ACCESSIBLE_ROLES to accessibleRoles
    )
}

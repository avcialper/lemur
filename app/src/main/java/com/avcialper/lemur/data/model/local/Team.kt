package com.avcialper.lemur.data.model.local

import com.avcialper.lemur.util.constant.Constants

data class Team(
    var id: String,
    var teamOwnerId: String,
    var name: String,
    var description: String,
    var imageUrl: String?,
    var members: List<Member>,
    var roles: List<Role>,
    var inviteCode: String,
    var rooms: List<String>
) {

    constructor() : this("", "", "", "", "", emptyList(), emptyList(), "", emptyList())

    fun toMap(): Map<String, Any?> =
        hashMapOf(
            Constants.TEAM_ID to id,
            Constants.TEAM_OWNER_ID to teamOwnerId,
            Constants.TEAM_NAME to name,
            Constants.TEAM_DESCRIPTION to description,
            Constants.IMAGE_URL to imageUrl,
            Constants.TEAM_MEMBERS to members,
            Constants.TEAM_ROLES to roles,
            Constants.TEAM_INVITE_CODE to inviteCode,
            Constants.TEAM_ROOMS to rooms
        )

    fun toTeamCard(): TeamCard = TeamCard(id, name, description, imageUrl)

}
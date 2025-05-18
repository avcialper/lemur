package com.avcialper.lemur.data.model.local

import com.avcialper.lemur.util.constant.Constants

data class Team(
    var id: String,
    var name: String,
    var description: String,
    var imageUrl: String?,
) {

    constructor() : this("", "", "", "")

    fun toMap(): Map<String, Any?> =
        hashMapOf(
            Constants.TEAM_ID to id,
            Constants.TEAM_NAME to name,
            Constants.TEAM_DESCRIPTION to description,
            Constants.IMAGE_URL to imageUrl
        )

}
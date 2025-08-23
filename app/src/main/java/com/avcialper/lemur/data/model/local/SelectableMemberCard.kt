package com.avcialper.lemur.data.model.local

data class SelectableMemberCard(
    val imageUrl: String?,
    val id: String,
    val name: String,
    var isSelected: Boolean = false
)

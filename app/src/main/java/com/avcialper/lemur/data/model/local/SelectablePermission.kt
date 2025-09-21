package com.avcialper.lemur.data.model.local

import com.avcialper.lemur.util.constant.Permissions

data class SelectablePermission(
    val permission: Permissions,
    var isSelected: Boolean
)
package com.avcialper.lemur.util.constant

import com.avcialper.lemur.R

enum class DateTimePickerType {
    DATE, TIME
}

enum class FilterType(val value: Int) {
    ALL(R.string.all),
    DATE(R.string.date),
    TODAY(R.string.today),
    PERSONAL(R.string.personal),
    TEAM(R.string.team),
    MEET(R.string.meet),
    CONTINUES(R.string.continues),
    COMPLETED(R.string.completed),
    CANCELED(R.string.canceled);

    companion object {
        val size = FilterType.entries.size

        fun fromIndex(index: Int): FilterType {
            return FilterType.entries[index]
        }

    }

}

enum class ResourceStatus {
    LOADING, SUCCESS, ERROR
}

enum class TaskStatus(val colorId: Int) {
    CONTINUES(R.color.orange), COMPLETED(R.color.chateau_green), CANCELED(R.color.red)
}

enum class TaskType(val messageId: Int, val drawableId: Int) {
    PERSONAL(R.string.task, R.drawable.ic_profile), TEAM(
        R.string.team_task,
        R.drawable.ic_team
    ),
    MEET(R.string.meet, R.drawable.ic_video_call)
}

enum class Theme(val value: String) {
    SYSTEM_DEFAULT("system_default"),
    LIGHT("light"),
    DARK("dark");

    companion object {
        fun fromString(theme: String?): Theme = entries.find { it.value == theme } ?: SYSTEM_DEFAULT
    }
}

enum class TeamBottomSheetActions {
    UPDATE, MEMBERS, INVITE_CODE, ROLES, LEAVE_TEAM, DELETE_TEAM
}

enum class Permissions(val label: Int) {
    TASK_MANAGEMENT(R.string.task_management_permission),
    ROOM_MANAGEMENT(R.string.room_management_permission),
    UPDATE_TEAM(R.string.update_team_permission),
    MEMBER_MANAGEMENT(R.string.member_management_permission),
    ROLE_MANAGEMENT(R.string.role_management_permission);

    companion object {
        val allRoles = listOf(
            TASK_MANAGEMENT.name,
            ROOM_MANAGEMENT.name,
            UPDATE_TEAM.name,
            MEMBER_MANAGEMENT.name,
            ROLE_MANAGEMENT.name
        )
    }
}

enum class RoleBottomSheetActions {
    MEMBERS, ADD_NEW_MEMBER, UPDATE, DELETE
}
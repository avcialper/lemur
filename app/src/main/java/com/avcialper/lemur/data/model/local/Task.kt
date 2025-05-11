package com.avcialper.lemur.data.model.local

import android.os.Parcelable
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var id: String,
    var ownerId: String,
    var subject: String,
    var description: String,
    var startDate: String,
    var endDate: String?,
    var startTime: String,
    var endTime: String,
    var imageUrl: String?,
    var type: TaskType,
    var status: TaskStatus,
    var notes: List<Note>
) : Parcelable {
    constructor() : this(
        "",
        "",
        "",
        "",
        "",
        null,
        "",
        "",
        null,
        TaskType.PERSONAL,
        TaskStatus.CONTINUES,
        emptyList()
    )

    fun toMap(): HashMap<String, Any?> =
        hashMapOf(
            Constants.TASK_ID to id,
            Constants.OWNER_ID to ownerId,
            Constants.SUBJECT to subject,
            Constants.DESCRIPTION to description,
            Constants.START_DATE to startDate,
            Constants.END_DATE to endDate,
            Constants.START_TIME to startTime,
            Constants.END_TIME to endTime,
            Constants.IMAGE_URL to imageUrl,
            Constants.TYPE to type.name,
            Constants.STATUS to status.name,
            Constants.NOTES to notes
        )
}
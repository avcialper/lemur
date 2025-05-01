package com.avcialper.lemur.data.model.local

import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType

data class Task(
    val id: String,
    val ownerId: String,
    val name: String,
    val description: String,
    val startDate: String,
    val endDate: String?,
    val startTime: String,
    val endTime: String,
    val imageUrl: String?,
    val type: TaskType,
    val status: TaskStatus
)
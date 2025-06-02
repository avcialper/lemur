package com.avcialper.lemur.data.model.local

data class TaskLoadStatus(
    val allLoaded: Boolean,
    val todayIsEmpty: Boolean,
    val continuesIsEmpty: Boolean,
    val completedIsEmpty: Boolean,
    val canceledIsEmpty: Boolean,
    val hasError: Boolean = false
)
package com.avcialper.lemur.ui.tasks.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TaskCreateViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val imageUrl = MutableStateFlow<String?>(null)

    fun createTask(
        date: String,
        time: String,
        imageFile: File?,
        subject: String,
        content: String,
        type: TaskType
    ) =
        viewModelScope.launch {
            if (imageFile != null)
                uploadImage(imageFile)
            addTask(date, time, subject, content, type)
        }

    private suspend fun uploadImage(file: File) {
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                imageUrl.value = resource.data?.data?.url
            else if (resource is Resource.Error)
                _state.value = Resource.Error(resource.throwable!!)
        }

    }

    private suspend fun addTask(
        date: String,
        time: String,
        subject: String,
        content: String,
        type: TaskType
    ) {
        val splitDate = date.split("-")
        val startDate = splitDate[0].trim()
        val endDate = if (date.contains("-")) splitDate[1].trim() else ""

        val splitTime = time.split("-")
        val startTime = splitTime[0].trim()
        val endTime = splitTime[1].trim()

        val task = Task(
            "",
            UserManager.user!!.id,
            subject,
            content,
            startDate,
            endDate,
            startTime,
            endTime,
            imageUrl.value,
            type,
            TaskStatus.CONTINUES
        )

        storageRepository.createTask(task)
            .collect { resource ->
                _state.value = resource
            }
    }

}
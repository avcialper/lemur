package com.avcialper.lemur.ui.tasks.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.data.repository.storage.task.TaskRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TaskCreateViewModel @Inject constructor(
    private val storageRepository: StorageRepository,
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val imageUrl = MutableStateFlow<String?>(null)

    fun createTask(task: Task, imageFile: File?) = viewModelScope.launch {
        if (imageFile != null)
            uploadImage(imageFile)
        addTask(task)
    }

    private suspend fun uploadImage(file: File) {
        _state.update { Resource.Loading() }
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                imageUrl.update { resource.data?.data?.url }
            else if (resource is Resource.Error)
                _state.update { Resource.Error(resource.throwable!!) }
        }
    }

    private suspend fun addTask(task: Task) {
        val id = UUID.randomUUID().toString()
        task.id = id
        task.ownerId = UserManager.user!!.id
        task.imageUrl = imageUrl.value

        taskRepository.createTask(task).collect { resource ->
            _state.update { resource }
        }
    }

}
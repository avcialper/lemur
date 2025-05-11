package com.avcialper.lemur.ui.tasks.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class TaskUpdateViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    private val _deleteState = MutableStateFlow<Resource<Boolean>?>(null)
    val deleteState = _deleteState.asStateFlow()

    private val imageUrl = MutableStateFlow<String?>(null)

    fun updateTask(task: Task, imageFile: File?) = viewModelScope.launch {
        if (imageFile != null)
            uploadImage(imageFile)
        update(task)
    }

    private suspend fun uploadImage(file: File) {
        storageRepository.uploadImage(file).collect { resource ->
            if (resource is Resource.Success)
                imageUrl.update { resource.data?.data?.url }
            else if (resource is Resource.Error)
                _state.update { Resource.Error(resource.throwable!!) }
        }
    }

    private suspend fun update(task: Task) {
        storageRepository.updateTask(task).collect { resource ->
            _state.update { resource }
        }
    }

    fun deleteTask(id: String) = viewModelScope.launch {
        storageRepository.deleteTask(id).collect { resource ->
            _deleteState.update { resource }
        }
    }

}
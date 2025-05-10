package com.avcialper.lemur.ui.tasks.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Task>?>(null)
    val state = _state.asStateFlow()

    fun getTaskDetail(taskId: String) = viewModelScope.launch {
        storageRepository.getTaskDetail(taskId).collect {
            _state.value = it
        }
    }

}
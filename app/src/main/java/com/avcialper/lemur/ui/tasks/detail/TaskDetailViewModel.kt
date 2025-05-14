package com.avcialper.lemur.ui.tasks.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.getCurrentDate
import com.avcialper.lemur.util.getCurrentTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<Task>?>(null)
    val state = _state.asStateFlow()

    private val _noteState = MutableStateFlow<Resource<Boolean>?>(null)
    val noteState = _noteState.asStateFlow()

    private val _statusState = MutableStateFlow<Resource<Boolean>?>(null)
    val statusState = _statusState.asStateFlow()

    fun getTaskDetail(taskId: String) = viewModelScope.launch {
        storageRepository.getTaskDetail(taskId).collect { resource ->
            _state.update { resource }
        }
    }

    fun addNote(taskId: String, note: String) = viewModelScope.launch {
        val ownerId = UserManager.user!!.id
        val noteData = Note(note, ownerId, getCurrentDate(), getCurrentTime())
        storageRepository.addNote(taskId, noteData).collect { resource ->
            _noteState.update { resource }
        }
    }

    fun updateTaskStatus(taskId: String, status: TaskStatus) = viewModelScope.launch {
        storageRepository.updateTaskStatus(taskId, status).collect { resource ->

        }
    }

    fun clearNoteState() {
        _noteState.update { null }
    }

    fun clearStatusState() {
        _statusState.update { null }
    }

}
package com.avcialper.lemur.ui.tasks.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.data.repository.storage.task.TaskRepository
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksFilterViewModel @Inject constructor(private val taskRepository: TaskRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<List<TaskCard>>>(Resource.Loading())
    val state = _state.asStateFlow()

    fun getAllTasks() = viewModelScope.launch {
        taskRepository.getUserTasks().collect(::collector)
    }

    fun getTasksByDate(date: String) = viewModelScope.launch {
        taskRepository.getSelectedDateTasks(date).collect(::collector)
    }

    fun getTodayTasks() = viewModelScope.launch {
        val today = getCurrentDate()
        taskRepository.getSelectedDateTasks(today).collect(::collector)
    }

    fun getPersonalTasks() = viewModelScope.launch {
        taskRepository.getPersonalTasks().collect(::collector)
    }

    fun getTeamTasks() = viewModelScope.launch {
        taskRepository.getTeamTasks().collect(::collector)
    }

    fun getMeets() = viewModelScope.launch {
        taskRepository.getMeets().collect(::collector)
    }

    fun getContinuesTasks() = viewModelScope.launch {
        taskRepository.getContinuesTasks().collect(::collector)
    }

    fun getCompletedTasks() = viewModelScope.launch {
        taskRepository.getCompletedTasks().collect(::collector)
    }

    fun getCanceledTasks() = viewModelScope.launch {
        taskRepository.getUserTasks().collect(::collector)
    }

    private fun collector(resource: Resource<List<TaskCard>>) {
        _state.update { resource }
    }

}
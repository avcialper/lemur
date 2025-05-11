package com.avcialper.lemur.ui.tasks.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksFilterViewModel @Inject constructor(
    private val repository: StorageRepository
) : ViewModel() {

    private val _state = MutableStateFlow<Resource<List<Task>>>(Resource.Loading())
    val state = _state.asStateFlow()

    fun getAllTasks() = viewModelScope.launch {
        repository.getUserTasks().collect(::collector)
    }

    fun getTasksByDate(date: String) = viewModelScope.launch {
        repository.getSelectedDateTasks(date).collect(::collector)
    }

    fun getTodayTasks() = viewModelScope.launch {
        val today = getCurrentDate()
        repository.getSelectedDateTasks(today).collect(::collector)
    }

    fun getPersonalTasks() = viewModelScope.launch {
        repository.getPersonalTasks().collect(::collector)
    }

    fun getTeamTasks() = viewModelScope.launch {
        repository.getTeamTasks().collect(::collector)
    }

    fun getMeets() = viewModelScope.launch {
        repository.getMeets().collect(::collector)
    }

    fun getContinuesTasks() = viewModelScope.launch {
        repository.getContinuesTasks().collect(::collector)
    }

    fun getCompletedTasks() = viewModelScope.launch {
        repository.getCompletedTasks().collect(::collector)
    }

    fun getCanceledTasks() = viewModelScope.launch {
        repository.getUserTasks().collect(::collector)
    }

    private fun collector(resource: Resource<List<Task>>) {
        _state.update { resource }
    }

}
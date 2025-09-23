package com.avcialper.lemur.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.data.model.local.TaskLoadStatus
import com.avcialper.lemur.data.repository.storage.task.TaskRepository
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.formatDate
import com.avcialper.owlcalendar.data.models.StartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    private val loading = Resource.Loading<List<TaskCard>>()

    private val _selectedDateTasks = MutableStateFlow<Resource<List<TaskCard>>>(loading)
    val selectedDateTasks = _selectedDateTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<Resource<List<TaskCard>>>(loading)
    val todayTasks = _todayTasks.asStateFlow()

    private val _continuesTasks = MutableStateFlow<Resource<List<TaskCard>>>(loading)
    val continuesTasks = _continuesTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<Resource<List<TaskCard>>>(loading)
    val completedTasks = _completedTasks.asStateFlow()

    private val _canceledTasks = MutableStateFlow<Resource<List<TaskCard>>>(loading)
    val canceledTasks = _canceledTasks.asStateFlow()

    val taskLoadStatus: StateFlow<TaskLoadStatus> = combine(
        todayTasks,
        continuesTasks,
        completedTasks,
        canceledTasks
    ) { today, continues, completed, canceled ->

        val allSuccess =
            listOf(today, continues, completed, canceled).all { it is Resource.Success }

        val hasError = listOf(today, continues, completed, canceled).any { it is Resource.Error }

        TaskLoadStatus(
            allLoaded = allSuccess,
            todayIsEmpty = (today as? Resource.Success)?.data?.isEmpty() ?: true,
            continuesIsEmpty = (continues as? Resource.Success)?.data?.isEmpty() ?: true,
            completedIsEmpty = (completed as? Resource.Success)?.data?.isEmpty() ?: true,
            canceledIsEmpty = (canceled as? Resource.Success)?.data?.isEmpty() ?: true,
            hasError = hasError
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        TaskLoadStatus(
            allLoaded = false,
            todayIsEmpty = true,
            continuesIsEmpty = true,
            completedIsEmpty = true,
            canceledIsEmpty = true
        )
    )

    var date: StartDate? = null
    var todayDate: StartDate? = null
    var scrollPosition = 0

    fun getSelectedDateTasks() = viewModelScope.launch {
        val formattedDate = formatDate(date!!)
        taskRepository.getSelectedDateTasksWithLimit(formattedDate).collect { resource ->
            _selectedDateTasks.update { resource }
        }
    }

    fun getTodayTasks() = viewModelScope.launch {
        val formattedDate = formatDate(todayDate!!)
        taskRepository.getSelectedDateTasksWithLimit(formattedDate).collect { resource ->
            _todayTasks.update { resource }
        }
    }

    fun getContinuesTasks() = viewModelScope.launch {
        taskRepository.getContinuesTasksWithLimit().collect { resource ->
            _continuesTasks.update { resource }
        }
    }

    fun getCompletedTasks() = viewModelScope.launch {
        taskRepository.getCompletedTasksWithLimit().collect { resource ->
            _completedTasks.update { resource }
        }
    }

    fun getCanceledTasks() = viewModelScope.launch {
        taskRepository.getCanceledTasksWithLimit().collect { resource ->
            _canceledTasks.update { resource }
        }
    }

}
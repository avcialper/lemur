package com.avcialper.lemur.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.repository.storage.StorageRepository
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.formatDate
import com.avcialper.owlcalendar.data.models.StartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val loading = Resource.Loading<List<Task>>()

    private val _selectedDateTasks = MutableStateFlow<Resource<List<Task>>>(loading)
    val selectedDateTasks = _selectedDateTasks.asStateFlow()

    private val _todayTasks = MutableStateFlow<Resource<List<Task>>>(loading)
    val todayTasks = _todayTasks.asStateFlow()

    private val _continuesTasks = MutableStateFlow<Resource<List<Task>>>(loading)
    val continuesTasks = _continuesTasks.asStateFlow()

    private val _completedTasks = MutableStateFlow<Resource<List<Task>>>(loading)
    val completedTasks = _completedTasks.asStateFlow()

    private val _canceledTasks = MutableStateFlow<Resource<List<Task>>>(loading)
    val canceledTasks = _canceledTasks.asStateFlow()

    var date: StartDate? = null
    var todayDate: StartDate? = null
    var scrollPosition = 0

    fun getSelectedDateTasks() = viewModelScope.launch {
        val formattedDate = formatDate(date!!)
        storageRepository.getSelectedDateTasksWithLimit(formattedDate).collect { resource ->
            _selectedDateTasks.update { resource }
        }
    }

    fun getTodayTasks() = viewModelScope.launch {
        val formattedDate = formatDate(todayDate!!)
        storageRepository.getSelectedDateTasksWithLimit(formattedDate).collect { resource ->
            _todayTasks.update { resource }
        }
    }

    fun getContinuesTasks() = viewModelScope.launch {
        storageRepository.getContinuesTasksWithLimit().collect { resource ->
            _continuesTasks.update { resource }
        }
    }

    fun getCompletedTasks() = viewModelScope.launch {
        storageRepository.getCompletedTasksWithLimit().collect { resource ->
            _completedTasks.update { resource }
        }
    }

    fun getCanceledTasks() = viewModelScope.launch {
        storageRepository.getCanceledTasksWithLimit().collect { resource ->
            _canceledTasks.update { resource }
        }
    }

}
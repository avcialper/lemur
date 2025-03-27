package com.avcialper.lemur.ui.tasks

import androidx.lifecycle.ViewModel
import com.avcialper.lemur.util.constant.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor() : ViewModel() {

    var filterType = FilterType.ALL
    var filterDate: String? = null

}
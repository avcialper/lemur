package com.avcialper.lemur.ui.tasks.filter

import androidx.lifecycle.ViewModel
import com.avcialper.lemur.util.constant.FilterType
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TasksFilterViewModel @Inject constructor() : ViewModel() {

    var filterType: FilterType? = null
    var filterDate: String? = null

}
package com.avcialper.lemur.ui.home

import androidx.lifecycle.ViewModel
import com.avcialper.owlcalendar.data.models.StartDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    var date: StartDate? = null

}
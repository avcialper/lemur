package com.avcialper.lemur.ui.team.room.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.data.repository.storage.room.RoomRepository
import com.avcialper.lemur.util.constant.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateRoomViewModel @Inject constructor(private val roomRepository: RoomRepository) :
    ViewModel() {

    private val _state = MutableStateFlow<Resource<Boolean>?>(null)
    val state = _state.asStateFlow()

    fun createRoom(room: Room) = viewModelScope.launch {
        roomRepository.createRoom(room).collect { resource ->
            _state.update { resource }
        }
    }
}
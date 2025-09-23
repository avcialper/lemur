package com.avcialper.lemur.data.repository.storage.room

import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow

interface RoomRepository {
    suspend fun createRoom(room: Room): Flow<Resource<Boolean>>
    suspend fun getRooms(rooms: List<String>): Flow<Resource<List<Room>>>
}
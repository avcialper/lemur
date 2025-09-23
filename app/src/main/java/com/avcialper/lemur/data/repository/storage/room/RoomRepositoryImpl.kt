package com.avcialper.lemur.data.repository.storage.room

import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomRepositoryImpl @Inject constructor(db: FirebaseFirestore) : RoomRepository {
    private val teamCollection = db.collection(Constants.TEAMS_COLLECTION)
    private val roomCollection = db.collection(Constants.ROOM_COLLECTION)

    override suspend fun createRoom(room: Room): Flow<Resource<Boolean>> = flowWithResource {
        roomCollection.document(room.id).set(room.toMap()).await()
        teamCollection.document(room.teamId)
            .update(Constants.TEAM_ROOMS, FieldValue.arrayUnion(room.id)).await()
        true
    }

    override suspend fun getRooms(rooms: List<String>): Flow<Resource<List<Room>>> =
        flowWithResource {
            val response = mutableListOf<Room>()
            rooms.forEach { id ->
                val roomDocument = roomCollection.document(id).get().await()
                response.add(roomDocument.toObject(Room::class.java)!!)
            }
            response
        }
}
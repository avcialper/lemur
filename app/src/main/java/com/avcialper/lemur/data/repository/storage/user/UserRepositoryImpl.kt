package com.avcialper.lemur.data.repository.storage.user

import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(db: FirebaseFirestore) : UserRepository {
    private val userCollection = db.collection(Constants.USERS_COLLECTION)

    override suspend fun createUser(userProfile: UserProfile): Flow<Resource<Boolean>> =
        flowWithResource {
            userCollection.document(userProfile.id).set(userProfile.toMap()).await()
            true
        }

    override suspend fun getUser(id: String): Flow<Resource<UserProfile>> = flowWithResource {
        val response = userCollection.document(id).get().await()
        response.toObject(UserProfile::class.java)!!
    }

    override suspend fun updateUser(userProfile: UserProfile): Flow<Resource<Boolean>> =
        flowWithResource {
            userCollection.document(userProfile.id).set(userProfile).await()
            true
        }
}
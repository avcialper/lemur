package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.BuildConfig
import com.avcialper.lemur.data.model.ImgBBResponse
import com.avcialper.lemur.data.model.UserProfile
import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val api: StorageApi,
    db: FirebaseFirestore
) : StorageRepository {

    private val collection = db.collection("users")

    override fun uploadImage(file: File): Flow<Resource<ImgBBResponse>> = flow {
        emit(Resource.Loading())
        try {
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val image = MultipartBody.Part.createFormData("image", file.name, requestFile)
            val apiKey = BuildConfig.IMG_BB_API_KEY.toRequestBody("text/plain".toMediaTypeOrNull())

            val response = api.uploadImage(image, apiKey)
            emit(Resource.Success(response))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }

    override fun createUser(userProfile: UserProfile): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())

        val (id, username, about, imageUrl) = userProfile

        val user = hashMapOf(
            "id" to id,
            "username" to username,
            "about" to about,
            "imageUrl" to imageUrl,
        )

        try {
            collection.document(userProfile.id).set(user).await()
            emit(Resource.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }

    override fun getUser(id: String): Flow<Resource<UserProfile>> = flow {
        emit(Resource.Loading())
        try {
            val response = collection.document(id).get().await()
            val user = response.toObject(UserProfile::class.java)
            emit(Resource.Success(user))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }

    override fun updateUser(userProfile: UserProfile): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading())
        try {
            collection.document(userProfile.id).set(userProfile)
            emit(Resource.Success(true))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }
}
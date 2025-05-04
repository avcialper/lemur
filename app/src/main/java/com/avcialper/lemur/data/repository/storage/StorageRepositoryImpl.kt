package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.BuildConfig
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.remote.ImgBBResponse
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
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

    private val userCollection = db.collection(Constants.USERS_COLLECTION)
    private val taskCollection = db.collection(Constants.TASKS_COLLECTION)

    override fun uploadImage(file: File): Flow<Resource<ImgBBResponse>> = flowWithResource {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val apiKey = BuildConfig.IMG_BB_API_KEY.toRequestBody("text/plain".toMediaTypeOrNull())

        api.uploadImage(image, apiKey)
    }

    override fun createUser(userProfile: UserProfile): Flow<Resource<Boolean>> = flowWithResource {
        val (id, username, about, imageUrl) = userProfile

        val user = hashMapOf(
            Constants.USER_ID to id,
            Constants.USERNAME to username,
            Constants.ABOUT to about,
            Constants.IMAGE_URL to imageUrl,
        )

        userCollection.document(userProfile.id).set(user).await()
        true
    }

    override fun getUser(id: String): Flow<Resource<UserProfile>> = flowWithResource {
        val response = userCollection.document(id).get().await()
        response.toObject(UserProfile::class.java)!!
    }

    override fun updateUser(userProfile: UserProfile): Flow<Resource<Boolean>> = flowWithResource {
        userCollection.document(userProfile.id).set(userProfile).await()
        true
    }

    override fun createTask(task: Task): Flow<Resource<Boolean>> = flowWithResource {
        val (_, ownerId, name, description, startDate, endDate, startTime, endTime, imageUrl, type, status) = task

        val data = hashMapOf(
            Constants.OWNER_ID to ownerId,
            Constants.NAME to name,
            Constants.DESCRIPTION to description,
            Constants.START_DATE to startDate,
            Constants.END_DATE to endDate,
            Constants.START_TIME to startTime,
            Constants.END_TIME to endTime,
            Constants.IMAGE_URL to imageUrl,
            Constants.TYPE to type.name,
            Constants.STATUS to status.name
        )

        taskCollection.document().set(data).await()
        true
    }

    override fun getSelectedDateTasks(date: String): Flow<Resource<List<Task>>> =
        getTasksByField(Constants.START_DATE, date)

    override fun getContinuesTasks(): Flow<Resource<List<Task>>> =
        getTasksByField(Constants.STATUS, TaskStatus.CONTINUES.name)

    override fun getCompletedTasks(): Flow<Resource<List<Task>>> =
        getTasksByField(Constants.STATUS, TaskStatus.COMPLETED.name)

    override fun getCanceledTasks(): Flow<Resource<List<Task>>> =
        getTasksByField(Constants.STATUS, TaskStatus.CANCELED.name)

    override fun getSelectedDateTasksWithLimit(date: String): Flow<Resource<List<Task>>> =
        getTasksByFieldWithLimit(Constants.START_DATE, date)

    override fun getContinuesTasksWithLimit(): Flow<Resource<List<Task>>> =
        getTasksByFieldWithLimit(Constants.STATUS, TaskStatus.CONTINUES.name)

    override fun getCompletedTasksWithLimit(): Flow<Resource<List<Task>>> =
        getTasksByFieldWithLimit(Constants.STATUS, TaskStatus.COMPLETED.name)

    override fun getCanceledTasksWithLimit(): Flow<Resource<List<Task>>> =
        getTasksByFieldWithLimit(Constants.STATUS, TaskStatus.CANCELED.name)

    override fun getUserTasks(): Flow<Resource<List<Task>>> = flowWithResource {
        val ownerId = UserManager.user!!.id
        val documents = taskCollection.whereEqualTo(Constants.OWNER_ID, ownerId).get().await()
        documents.toObjects(Task::class.java)
    }

    private fun <T> getTasksByField(filed: String, value: T): Flow<Resource<List<Task>>> =
        flowWithResource {
            val ownerId = UserManager.user!!.id
            val documents =
                taskCollection.whereEqualTo(Constants.OWNER_ID, ownerId).whereEqualTo(filed, value)
                    .get()
                    .await()
            documents.toObjects(Task::class.java)
        }

    private fun <T> getTasksByFieldWithLimit(filed: String, value: T): Flow<Resource<List<Task>>> =
        flowWithResource {
            val ownerId = UserManager.user!!.id
            val documents =
                taskCollection.whereEqualTo(Constants.OWNER_ID, ownerId).whereEqualTo(filed, value)
                    .limit(3)
                    .get()
                    .await()
            documents.toObjects(Task::class.java)
        }
}
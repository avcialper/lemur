package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.BuildConfig
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.model.local.TeamCard
import com.avcialper.lemur.data.model.remote.ImgBBResponse
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType
import com.google.firebase.firestore.FieldValue
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
    private val teamCollection = db.collection(Constants.TEAMS_COLLECTION)
    private val roomCollection = db.collection(Constants.ROOM_COLLECTION)

    override suspend fun uploadImage(file: File): Flow<Resource<ImgBBResponse>> = flowWithResource {
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val image = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val apiKey = BuildConfig.IMG_BB_API_KEY.toRequestBody("text/plain".toMediaTypeOrNull())

        api.uploadImage(image, apiKey)
    }

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

    override suspend fun createTask(task: Task): Flow<Resource<Boolean>> = flowWithResource {
        taskCollection.document(task.id).set(task.toMap()).await()
        true
    }

    override suspend fun getSelectedDateTasks(date: String): Flow<Resource<List<TaskCard>>> =
        getTasksByField(Constants.START_DATE, date)

    override suspend fun getContinuesTasks(): Flow<Resource<List<TaskCard>>> =
        getTasksByField(Constants.STATUS, TaskStatus.CONTINUES.name)

    override suspend fun getCompletedTasks(): Flow<Resource<List<TaskCard>>> =
        getTasksByField(Constants.STATUS, TaskStatus.COMPLETED.name)

    override suspend fun getCanceledTasks(): Flow<Resource<List<TaskCard>>> =
        getTasksByField(Constants.STATUS, TaskStatus.CANCELED.name)

    override suspend fun getPersonalTasks(): Flow<Resource<List<TaskCard>>> =
        getTasksByField(Constants.TYPE, TaskType.PERSONAL.name)

    override suspend fun getTeamTasks(): Flow<Resource<List<TaskCard>>> =
        getTasksByField(Constants.TYPE, TaskType.TEAM.name)

    override suspend fun getMeets(): Flow<Resource<List<TaskCard>>> =
        getTasksByField(Constants.TYPE, TaskType.MEET.name)

    override suspend fun getSelectedDateTasksWithLimit(date: String): Flow<Resource<List<TaskCard>>> =
        getTasksByFieldWithLimit(Constants.START_DATE, date)

    override suspend fun getContinuesTasksWithLimit(): Flow<Resource<List<TaskCard>>> =
        getTasksByFieldWithLimit(Constants.STATUS, TaskStatus.CONTINUES.name)

    override suspend fun getCompletedTasksWithLimit(): Flow<Resource<List<TaskCard>>> =
        getTasksByFieldWithLimit(Constants.STATUS, TaskStatus.COMPLETED.name)

    override suspend fun getCanceledTasksWithLimit(): Flow<Resource<List<TaskCard>>> =
        getTasksByFieldWithLimit(Constants.STATUS, TaskStatus.CANCELED.name)

    override suspend fun getUserTasks(): Flow<Resource<List<TaskCard>>> = flowWithResource {
        val ownerId = UserManager.user!!.id
        val documents = taskCollection.whereEqualTo(Constants.OWNER_ID, ownerId).get().await()
        documents.toObjects(Task::class.java).map { it.toTaskCard() }
    }

    override suspend fun getTaskDetail(taskId: String): Flow<Resource<Task>> = flowWithResource {
        val taskDocument = taskCollection.whereEqualTo(Constants.TASK_ID, taskId).get().await()
        taskDocument.toObjects(Task::class.java).first()
    }

    override suspend fun updateTask(task: Task): Flow<Resource<Boolean>> = flowWithResource {
        taskCollection.document(task.id).update(task.toMap()).await()
        true
    }

    override suspend fun deleteTask(id: String): Flow<Resource<Boolean>> = flowWithResource {
        taskCollection.document(id).delete().await()
        true
    }

    override suspend fun updateTaskStatus(id: String, status: TaskStatus): Flow<Resource<Boolean>> =
        flowWithResource {
            taskCollection.document(id).update(Constants.STATUS, status.name).await()
            true
        }

    override suspend fun addNote(id: String, note: Note): Flow<Resource<Boolean>> =
        flowWithResource {
            taskCollection.document(id).update(Constants.NOTES, FieldValue.arrayUnion(note)).await()
            true
        }

    override suspend fun createTeam(team: Team): Flow<Resource<Boolean>> = flowWithResource {
        teamCollection.document(team.id).set(team.toMap()).await()
        true
    }

    override suspend fun addTeamToUser(userId: String, teamId: String): Flow<Resource<Boolean>> =
        flowWithResource {
            userCollection.document(userId).update(Constants.TEAMS, FieldValue.arrayUnion(teamId))
                .await()
            true
        }

    override suspend fun getUsersJoinedTeams(userId: String): Flow<Resource<List<TeamCard>>> =
        flowWithResource {
            val document = userCollection.document(userId).get().await()
            val teams = document.toObject(UserProfile::class.java)?.teams ?: emptyList()

            val response = mutableListOf<TeamCard>()
            teams.forEach { id ->
                val teamDocument = teamCollection.whereEqualTo(Constants.TEAM_ID, id).get().await()
                val teamList = teamDocument.toObjects(Team::class.java).map { it.toTeamCard() }
                response.addAll(teamList)
            }
            response
        }

    override suspend fun joinTeam(inviteCode: String, userId: String): Flow<Resource<Boolean>> =
        flowWithResource {
            val teamDocuments =
                teamCollection.whereEqualTo(Constants.TEAM_INVITE_CODE, inviteCode).get().await()
            if (teamDocuments.isEmpty)
                return@flowWithResource false

            val teamDocument = teamDocuments.documents.first()
            val team = teamDocument.toObject(Team::class.java)!!

            if (team.members.any { it.id == userId })
                return@flowWithResource false

            val member = Member(userId, "MEMBER")
            val members = team.members + member

            teamCollection.document(team.id).update(Constants.TEAM_MEMBERS, members).await()
            addTeamToUser(userId, team.id).collect {}

            true
        }

    override suspend fun getTeam(teamId: String): Flow<Resource<Team>> = flowWithResource {
        val documentation = teamCollection.document(teamId).get().await()
        documentation.toObject(Team::class.java)!!
    }

    override suspend fun createRoom(room: Room): Flow<Resource<Boolean>> = flowWithResource {
        roomCollection.document(room.id).set(room.toMap()).await()
        teamCollection.document(room.teamId)
            .update(Constants.TEAM_ROOMS, FieldValue.arrayUnion(room.id)).await()
        true
    }

    private fun <T> getTasksByField(filed: String, value: T): Flow<Resource<List<TaskCard>>> =
        flowWithResource {
            val ownerId = UserManager.user!!.id
            val documents =
                taskCollection.whereEqualTo(Constants.OWNER_ID, ownerId).whereEqualTo(filed, value)
                    .get()
                    .await()
            documents.toObjects(Task::class.java).map { it.toTaskCard() }
        }

    private fun <T> getTasksByFieldWithLimit(
        filed: String,
        value: T
    ): Flow<Resource<List<TaskCard>>> =
        flowWithResource {
            val ownerId = UserManager.user!!.id
            val documents =
                taskCollection.whereEqualTo(Constants.OWNER_ID, ownerId).whereEqualTo(filed, value)
                    .limit(3)
                    .get()
                    .await()
            documents.toObjects(Task::class.java).map { it.toTaskCard() }
        }
}
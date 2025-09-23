package com.avcialper.lemur.data.repository.storage.task

import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskStatus
import com.avcialper.lemur.util.constant.TaskType
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepositoryImpl @Inject constructor(db: FirebaseFirestore) : TaskRepository {

    private val taskCollection = db.collection(Constants.TASKS_COLLECTION)

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
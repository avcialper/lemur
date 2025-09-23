package com.avcialper.lemur.data.repository.storage.task

import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskStatus
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    suspend fun createTask(task: Task): Flow<Resource<Boolean>>
    suspend fun getSelectedDateTasks(date: String): Flow<Resource<List<TaskCard>>>
    suspend fun getContinuesTasks(): Flow<Resource<List<TaskCard>>>
    suspend fun getCompletedTasks(): Flow<Resource<List<TaskCard>>>
    suspend fun getCanceledTasks(): Flow<Resource<List<TaskCard>>>
    suspend fun getPersonalTasks(): Flow<Resource<List<TaskCard>>>
    suspend fun getTeamTasks(): Flow<Resource<List<TaskCard>>>
    suspend fun getMeets(): Flow<Resource<List<TaskCard>>>
    suspend fun getSelectedDateTasksWithLimit(date: String): Flow<Resource<List<TaskCard>>>
    suspend fun getContinuesTasksWithLimit(): Flow<Resource<List<TaskCard>>>
    suspend fun getCompletedTasksWithLimit(): Flow<Resource<List<TaskCard>>>
    suspend fun getCanceledTasksWithLimit(): Flow<Resource<List<TaskCard>>>
    suspend fun getUserTasks(): Flow<Resource<List<TaskCard>>>
    suspend fun getTaskDetail(taskId: String): Flow<Resource<Task>>
    suspend fun updateTask(task: Task): Flow<Resource<Boolean>>
    suspend fun deleteTask(id: String): Flow<Resource<Boolean>>
    suspend fun addNote(id: String, note: Note): Flow<Resource<Boolean>>
    suspend fun updateTaskStatus(id: String, status: TaskStatus): Flow<Resource<Boolean>>
}
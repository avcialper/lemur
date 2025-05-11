package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.remote.ImgBBResponse
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    fun uploadImage(file: File): Flow<Resource<ImgBBResponse>>
    fun createUser(userProfile: UserProfile): Flow<Resource<Boolean>>
    fun getUser(id: String): Flow<Resource<UserProfile>>
    fun updateUser(userProfile: UserProfile): Flow<Resource<Boolean>>
    fun createTask(task: Task): Flow<Resource<Boolean>>
    fun getSelectedDateTasks(date: String): Flow<Resource<List<Task>>>
    fun getContinuesTasks(): Flow<Resource<List<Task>>>
    fun getCompletedTasks(): Flow<Resource<List<Task>>>
    fun getCanceledTasks(): Flow<Resource<List<Task>>>
    fun getPersonalTasks(): Flow<Resource<List<Task>>>
    fun getTeamTasks(): Flow<Resource<List<Task>>>
    fun getMeets(): Flow<Resource<List<Task>>>
    fun getSelectedDateTasksWithLimit(date: String): Flow<Resource<List<Task>>>
    fun getContinuesTasksWithLimit(): Flow<Resource<List<Task>>>
    fun getCompletedTasksWithLimit(): Flow<Resource<List<Task>>>
    fun getCanceledTasksWithLimit(): Flow<Resource<List<Task>>>
    fun getUserTasks(): Flow<Resource<List<Task>>>
    fun getTaskDetail(taskId: String): Flow<Resource<Task>>
    fun updateTask(task: Task): Flow<Resource<Boolean>>
    fun deleteTask(id: String): Flow<Resource<Boolean>>
}
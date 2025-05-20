package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.model.remote.ImgBBResponse
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.constant.TaskStatus
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    suspend fun uploadImage(file: File): Flow<Resource<ImgBBResponse>>
    suspend fun createUser(userProfile: UserProfile): Flow<Resource<Boolean>>
    suspend fun getUser(id: String): Flow<Resource<UserProfile>>
    suspend fun updateUser(userProfile: UserProfile): Flow<Resource<Boolean>>
    suspend fun createTask(task: Task): Flow<Resource<Boolean>>
    suspend fun getSelectedDateTasks(date: String): Flow<Resource<List<Task>>>
    suspend fun getContinuesTasks(): Flow<Resource<List<Task>>>
    suspend fun getCompletedTasks(): Flow<Resource<List<Task>>>
    suspend fun getCanceledTasks(): Flow<Resource<List<Task>>>
    suspend fun getPersonalTasks(): Flow<Resource<List<Task>>>
    suspend fun getTeamTasks(): Flow<Resource<List<Task>>>
    suspend fun getMeets(): Flow<Resource<List<Task>>>
    suspend fun getSelectedDateTasksWithLimit(date: String): Flow<Resource<List<Task>>>
    suspend fun getContinuesTasksWithLimit(): Flow<Resource<List<Task>>>
    suspend fun getCompletedTasksWithLimit(): Flow<Resource<List<Task>>>
    suspend fun getCanceledTasksWithLimit(): Flow<Resource<List<Task>>>
    suspend fun getUserTasks(): Flow<Resource<List<Task>>>
    suspend fun getTaskDetail(taskId: String): Flow<Resource<Task>>
    suspend fun updateTask(task: Task): Flow<Resource<Boolean>>
    suspend fun deleteTask(id: String): Flow<Resource<Boolean>>
    suspend fun addNote(id: String, note: Note): Flow<Resource<Boolean>>
    suspend fun updateTaskStatus(id: String, status: TaskStatus): Flow<Resource<Boolean>>
    suspend fun createTeam(team: Team): Flow<Resource<Boolean>>
    suspend fun addTeamToUser(userId: String, teamId: String): Flow<Resource<Boolean>>
    suspend fun getUsersJoinedTeams(userId: String): Flow<Resource<List<Team>>>
    suspend fun joinTeam(inviteCode: String, userId: String): Flow<Resource<Boolean>>
}
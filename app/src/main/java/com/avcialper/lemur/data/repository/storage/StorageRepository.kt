package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.model.local.TeamCard
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

    suspend fun createTeam(team: Team): Flow<Resource<Boolean>>
    suspend fun addTeamToUser(userId: String, teamId: String): Flow<Resource<Boolean>>
    suspend fun getUsersJoinedTeams(userId: String): Flow<Resource<List<TeamCard>>>
    suspend fun joinTeam(inviteCode: String, userId: String): Flow<Resource<Boolean>>
    suspend fun getTeam(teamId: String): Flow<Resource<Team>>
    suspend fun leaveTeam(teamId: String, member: Member): Flow<Resource<Boolean>>
    suspend fun changeTeamLead(teamId: String, newLeadId: String): Flow<Resource<Boolean>>
    suspend fun deleteTeam(teamId: String, memberIDs: List<String>): Flow<Resource<Boolean>>
    suspend fun updateTeam(
        teamId: String, imageUrl: String?, name: String, description: String
    ): Flow<Resource<Boolean>>

    suspend fun createRoom(room: Room): Flow<Resource<Boolean>>
    suspend fun getRooms(rooms: List<String>): Flow<Resource<List<Room>>>

    suspend fun getMembers(teamId: String): Flow<Resource<List<MemberCard>>>
    suspend fun removeMemberFromTeam(teamId: String, member: Member): Flow<Resource<Boolean>>

    suspend fun getRoles(teamId: String): Flow<Resource<List<Role>>>
    suspend fun getMembersByRole(teamId: String, roleCode: String): Flow<Resource<List<MemberCard>>>
    suspend fun removeRoleFromMember(
        teamId: String, memberId: String, roleCode: String
    ): Flow<Resource<Boolean>>

    suspend fun isUserHaveRoleManagementPermission(
        teamId: String, userId: String
    ): Flow<Resource<Boolean>>
}
package com.avcialper.lemur.data.repository.storage.team

import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.model.local.TeamCard
import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
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
    suspend fun getMembers(teamId: String): Flow<Resource<List<MemberCard>>>
    suspend fun removeMemberFromTeam(teamId: String, member: Member): Flow<Resource<Boolean>>
    suspend fun isUserHaveRoleManagementPermission(
        teamId: String, userId: String
    ): Flow<Resource<Boolean>>
}
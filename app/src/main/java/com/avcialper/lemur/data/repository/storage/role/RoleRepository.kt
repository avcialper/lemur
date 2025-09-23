package com.avcialper.lemur.data.repository.storage.role

import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow

interface RoleRepository {
    suspend fun getRoles(teamId: String): Flow<Resource<List<Role>>>
    suspend fun getMembersByRole(teamId: String, roleCode: String): Flow<Resource<List<MemberCard>>>
    suspend fun getMembersNotInRole(
        teamId: String,
        roleCode: String
    ): Flow<Resource<List<SelectableMemberCard>>>
    suspend fun getRole(teamId: String, roleCode: String): Flow<Resource<Role>>
    suspend fun updateRole(teamId: String, updatedRole: Role): Flow<Resource<Boolean>>
    suspend fun removeRoleFromMember(
        teamId: String, memberId: String, roleCode: String
    ): Flow<Resource<Boolean>>
    suspend fun assignRoleToMembers(
        teamId: String,
        memberIds: List<String>,
        roleCode: String
    ): Flow<Resource<Boolean>>
    suspend fun deleteRole(teamId: String, roleCode: String): Flow<Resource<Boolean>>
}
package com.avcialper.lemur.data.repository.storage.role

import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoleRepositoryImpl @Inject constructor(db: FirebaseFirestore) : RoleRepository {
    private val userCollection = db.collection(Constants.USERS_COLLECTION)
    private val teamCollection = db.collection(Constants.TEAMS_COLLECTION)

    override suspend fun getRoles(teamId: String): Flow<Resource<List<Role>>> = flowWithResource {
        val documents = teamCollection.document(teamId).get().await()
        val team = documents.toObject(Team::class.java)!!
        team.roles
    }

    override suspend fun getMembersByRole(
        teamId: String,
        roleCode: String
    ): Flow<Resource<List<MemberCard>>> = flowWithResource {
        val documents = teamCollection.document(teamId).get().await()
        val team = documents.toObject(Team::class.java)!!
        val members = team.members
        val roles = team.roles

        val filteredMembers = members.filter { member ->
            member.roleCodes.contains(roleCode)
        }

        filteredMembers.map { member ->
            val userDocument = userCollection.document(member.id).get().await()
            val user = userDocument.toObject(UserProfile::class.java)!!

            val permissions = member.roleCodes.flatMap { roleCode ->
                roles.find { role ->
                    role.code == roleCode
                }?.permissions ?: emptyList()
            }.distinct()

            member.toMemberCard(user.username, emptyList(), user.imageUrl, permissions)
        }

    }

    override suspend fun getMembersNotInRole(
        teamId: String,
        roleCode: String
    ): Flow<Resource<List<SelectableMemberCard>>> = flowWithResource {
        val documents = teamCollection.document(teamId).get().await()
        val team = documents.toObject(Team::class.java)
        val members = team?.members ?: emptyList()

        members.filter { member ->
            !member.roleCodes.contains(roleCode)
        }.map { member ->
            val userDocument = userCollection.document(member.id).get().await()
            val user = userDocument.toObject(UserProfile::class.java)!!

            member.toSelectableMemberCard(user.username, user.imageUrl)
        }
    }

    override suspend fun getRole(teamId: String, roleCode: String): Flow<Resource<Role>> =
        flowWithResource {
            val document = teamCollection.document(teamId).get().await()
            val team = document.toObject(Team::class.java)!!

            team.roles.find { role ->
                role.code == roleCode
            }!!
        }

    override suspend fun updateRole(teamId: String, updatedRole: Role): Flow<Resource<Boolean>> =
        flowWithResource {
            val document = teamCollection.document(teamId).get().await()
            val team = document.toObject(Team::class.java)!!
            val updateRoles = team.roles.map { role ->
                if (role.code == updatedRole.code) updatedRole else role
            }

            teamCollection.document(teamId).update(Constants.TEAM_ROLES, updateRoles).await()
            true
        }

    override suspend fun removeRoleFromMember(
        teamId: String,
        memberId: String,
        roleCode: String
    ): Flow<Resource<Boolean>> = flowWithResource {
        val document = teamCollection.document(teamId).get().await()
        val team = document.toObject(Team::class.java)!!
        val members = team.members.map { member ->
            if (member.id == memberId) {
                member.roleCodes = member.roleCodes.filter { it != roleCode }
            }
            member
        }
        teamCollection.document(teamId).update(Constants.TEAM_MEMBERS, members).await()
        true
    }

    override suspend fun assignRoleToMembers(
        teamId: String,
        memberIds: List<String>,
        roleCode: String
    ): Flow<Resource<Boolean>> = flowWithResource {
        val document = teamCollection.document(teamId).get().await()
        val team = document.toObject(Team::class.java)!!

        val updatedMembers = team.members.map { member ->
            if (memberIds.contains(member.id)) {
                member.copy(roleCodes = member.roleCodes + roleCode)
            } else {
                member
            }
        }

        teamCollection.document(teamId).update(Constants.TEAM_MEMBERS, updatedMembers).await()

        true
    }

    override suspend fun deleteRole(teamId: String, roleCode: String): Flow<Resource<Boolean>> =
        flowWithResource {
            val document = teamCollection.document(teamId).get().await()
            val team = document.toObject(Team::class.java)!!

            val updatedRoles = team.roles.filter { role -> role.code != roleCode }
            val updatedMembers = team.members.map { member ->
                member.copy(roleCodes = member.roleCodes.filter { code -> code != roleCode })
            }

            teamCollection.document(teamId).update(Constants.TEAM_ROLES, updatedRoles).await()
            teamCollection.document(teamId).update(Constants.TEAM_MEMBERS, updatedMembers).await()

            true
        }

}
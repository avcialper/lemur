package com.avcialper.lemur.data.repository.storage.team

import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.model.local.TeamCard
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Permissions
import com.avcialper.lemur.util.constant.Resource
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TeamRepositoryImpl @Inject constructor(db: FirebaseFirestore) : TeamRepository {
    private val teamCollection = db.collection(Constants.TEAMS_COLLECTION)
    private val userCollection = db.collection(Constants.USERS_COLLECTION)

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

            val member = Member(userId, listOf("MEMBER"))
            val members = team.members + member

            teamCollection.document(team.id).update(Constants.TEAM_MEMBERS, members).await()
            addTeamToUser(userId, team.id).collect {}

            true
        }

    override suspend fun getTeam(teamId: String): Flow<Resource<Team>> = flowWithResource {
        val documentation = teamCollection.document(teamId).get().await()
        documentation.toObject(Team::class.java)!!
    }

    override suspend fun leaveTeam(teamId: String, member: Member): Flow<Resource<Boolean>> =
        flowWithResource {
            userCollection.document(member.id)
                .update(Constants.TEAMS, FieldValue.arrayRemove(teamId))
                .await()
            teamCollection.document(teamId)
                .update(Constants.TEAM_MEMBERS, FieldValue.arrayRemove(member)).await()

            true
        }

    override suspend fun changeTeamLead(
        teamId: String,
        newLeadId: String
    ): Flow<Resource<Boolean>> = flowWithResource {
        val teamDocument = teamCollection.document(teamId).get().await()
        val team = teamDocument.toObject(Team::class.java)!!

        team.teamLeadId = newLeadId
        team.members.find { it.id == newLeadId }?.roleCodes = listOf("OWNER")
        teamCollection.document(teamId).set(team.toMap()).await()

        true
    }

    override suspend fun updateTeam(
        teamId: String,
        imageUrl: String?,
        name: String,
        description: String
    ): Flow<Resource<Boolean>> = flowWithResource {
        teamCollection.document(teamId).update(
            Constants.TEAM_NAME,
            name,
            Constants.TEAM_DESCRIPTION,
            description,
            Constants.IMAGE_URL,
            imageUrl
        ).await()
        true
    }

    override suspend fun getMembers(teamId: String): Flow<Resource<List<MemberCard>>> =
        flowWithResource {
            val documents = teamCollection.document(teamId).get().await()
            val members = documents.toObject(Team::class.java)?.members ?: emptyList()
            val roles = documents.toObject(Team::class.java)?.roles ?: emptyList()

            val memberCards = mutableListOf<MemberCard>()
            members.forEach { member ->
                val userDocument = userCollection.document(member.id).get().await()
                val user = userDocument.toObject(UserProfile::class.java)!!

                val roleNames = member.roleCodes.mapNotNull { code ->
                    roles.find { role -> role.code == code }?.name
                }

                val permissions = member.roleCodes.flatMap { roleCode ->
                    roles.find { role ->
                        role.code == roleCode
                    }?.permissions ?: emptyList()
                }.distinct()

                val memberCard =
                    member.toMemberCard(user.username, roleNames, user.imageUrl, permissions)
                memberCards.add(memberCard)
            }

            memberCards
        }

    override suspend fun removeMemberFromTeam(
        teamId: String,
        member: Member
    ): Flow<Resource<Boolean>> =
        flowWithResource {
            userCollection.document(member.id)
                .update(Constants.TEAMS, FieldValue.arrayRemove(teamId))
                .await()

            teamCollection.document(teamId)
                .update(Constants.TEAM_MEMBERS, FieldValue.arrayRemove(member)).await()

            true
        }

    override suspend fun isUserHaveRoleManagementPermission(
        teamId: String,
        userId: String
    ): Flow<Resource<Boolean>> = flowWithResource {
        val teamDocument = teamCollection.document(teamId).get().await()
        val team = teamDocument.toObject(Team::class.java)!!
        val user = team.members.find { member -> member.id == userId }
        val roles = team.roles

        val roleManagementPermissions = roles.filter { role ->
            role.permissions.contains(Permissions.ROLE_MANAGEMENT.name)
        }

        user?.roleCodes?.forEach { roleCode ->
            if (roleManagementPermissions.any { it.code == roleCode })
                return@flowWithResource true
        }

        false
    }

    override suspend fun deleteTeam(
        teamId: String,
        memberIDs: List<String>
    ): Flow<Resource<Boolean>> = flowWithResource {

        memberIDs.forEach { id ->
            userCollection.document(id).update(Constants.TEAMS, FieldValue.arrayRemove(teamId))
                .await()
        }

        teamCollection.document(teamId).delete().await()

        true
    }
}
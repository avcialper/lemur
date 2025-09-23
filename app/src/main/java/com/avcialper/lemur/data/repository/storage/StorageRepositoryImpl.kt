package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.BuildConfig
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.data.model.local.Note
import com.avcialper.lemur.data.model.local.Role
import com.avcialper.lemur.data.model.local.Room
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.data.model.local.Task
import com.avcialper.lemur.data.model.local.TaskCard
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.data.model.local.TeamCard
import com.avcialper.lemur.data.model.remote.ImgBBResponse
import com.avcialper.lemur.data.model.remote.UserProfile
import com.avcialper.lemur.data.repository.flowWithResource
import com.avcialper.lemur.data.repository.remote.StorageApi
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.constant.Permissions
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

    override suspend fun createRoom(room: Room): Flow<Resource<Boolean>> = flowWithResource {
        roomCollection.document(room.id).set(room.toMap()).await()
        teamCollection.document(room.teamId)
            .update(Constants.TEAM_ROOMS, FieldValue.arrayUnion(room.id)).await()
        true
    }

    override suspend fun getRooms(rooms: List<String>): Flow<Resource<List<Room>>> =
        flowWithResource {
            val response = mutableListOf<Room>()
            rooms.forEach { id ->
                val roomDocument = roomCollection.document(id).get().await()
                response.add(roomDocument.toObject(Room::class.java)!!)
            }
            response
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
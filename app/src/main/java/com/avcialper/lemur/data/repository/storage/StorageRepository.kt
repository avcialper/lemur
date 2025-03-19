package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.data.model.ImgBBResponse
import com.avcialper.lemur.data.model.UserProfile
import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    fun uploadImage(file: File): Flow<Resource<ImgBBResponse>>
    fun createUser(userProfile: UserProfile): Flow<Resource<Boolean>>
    fun getUser(id: String): Flow<Resource<UserProfile>>
    fun updateUser(userProfile: UserProfile): Flow<Resource<Boolean>>
}
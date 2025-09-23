package com.avcialper.lemur.data.repository.storage

import com.avcialper.lemur.data.model.remote.ImgBBResponse
import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow
import java.io.File

interface StorageRepository {
    suspend fun uploadImage(file: File): Flow<Resource<ImgBBResponse>>
}
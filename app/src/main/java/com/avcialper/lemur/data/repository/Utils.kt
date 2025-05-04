package com.avcialper.lemur.data.repository

import com.avcialper.lemur.util.constant.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

inline fun <T> flowWithResource(crossinline action: suspend () -> T): Flow<Resource<T>> =
    flow {
        emit(Resource.Loading())
        try {
            val result = action()
            emit(Resource.Success(result))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(Resource.Error(e))
        }
    }
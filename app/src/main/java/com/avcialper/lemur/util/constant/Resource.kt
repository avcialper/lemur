package com.avcialper.lemur.util.constant

sealed class Resource<T>(
    val data: T? = null,
    val throwable: Exception? = null,
    val status: ResourceStatus
) {
    class Loading<T> : Resource<T>(
        status = ResourceStatus.LOADING
    )

    class Success<T>(data: T?) : Resource<T>(
        data = data,
        status = ResourceStatus.SUCCESS
    )

    class Error<T>(exception: Exception?) : Resource<T>(
        throwable = exception,
        status = ResourceStatus.ERROR
    )
}
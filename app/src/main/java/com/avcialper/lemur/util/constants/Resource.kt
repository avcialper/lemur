package com.avcialper.lemur.util.constants

sealed class Resource<T>(
    val data: T? = null,
    val throwable: Throwable? = null,
    val status: ResourceStatus
) {
    class Loading<T> : Resource<T>(
        status = ResourceStatus.LOADING
    )

    class Success<T>(data: T?) : Resource<T>(
        data = data,
        status = ResourceStatus.SUCCESS
    )

    class Error<T>(exception: Throwable?) : Resource<T>(
        throwable = exception,
        status = ResourceStatus.ERROR
    )
}
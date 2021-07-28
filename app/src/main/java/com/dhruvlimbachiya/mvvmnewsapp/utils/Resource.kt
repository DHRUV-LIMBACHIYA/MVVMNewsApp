package com.dhruvlimbachiya.mvvmnewsapp.utils

/**
 * Created by Dhruv Limbachiya on 28-07-2021.
 */
sealed class Resource<T>(
   val data: T? = null,
   val message: String? = null
) {
    class Success<T>(data: T?) : Resource<T>(data)
    class Loading<T> : Resource<T>()
    class Error<T>(message: String?, data: T? = null) : Resource<T>(data, message)
}

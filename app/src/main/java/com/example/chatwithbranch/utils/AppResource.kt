package com.example.chatwithbranch.utils

/**
 * Sealed class representing different resource states in the application.
 *
 * @param T The type of data associated with the resource state.
 * @property data The actual data associated with the resource state.
 * @property message A message providing additional information about the resource state.
 */
sealed class AppResource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : AppResource<T>(data)
    class Error<T>(message: String, data: T? = null) : AppResource<T>(data, message)
    class Loading<T>(data: T? = null) : AppResource<T>(data)
}
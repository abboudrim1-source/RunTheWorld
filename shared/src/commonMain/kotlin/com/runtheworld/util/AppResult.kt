package com.runtheworld.util

sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : AppResult<Nothing>()

    val isSuccess get() = this is Success
    val isError get() = this is Error

    fun getOrNull(): T? = if (this is Success) data else null
    fun errorMessage(): String? = if (this is Error) message else null
}

inline fun <T> appRunCatching(block: () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (e: Exception) {
        AppResult.Error(e.message ?: "Unknown error", e)
    }

inline fun <T, R> AppResult<T>.map(transform: (T) -> R): AppResult<R> = when (this) {
    is AppResult.Success -> AppResult.Success(transform(data))
    is AppResult.Error -> this
}

inline fun <T> AppResult<T>.onSuccess(action: (T) -> Unit): AppResult<T> {
    if (this is AppResult.Success) action(data)
    return this
}

inline fun <T> AppResult<T>.onError(action: (String) -> Unit): AppResult<T> {
    if (this is AppResult.Error) action(message)
    return this
}

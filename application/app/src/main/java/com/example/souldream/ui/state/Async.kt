package com.example.souldream.ui.state

/**
 * Loading state of a piece of remote data. Every screen section is driven by one of
 * these, which is what lets the UI show shimmer placeholders instead of real data.
 */
sealed interface Async<out T> {
    data object Loading : Async<Nothing>
    data class Success<out T>(val data: T) : Async<T>
    data class Failure(val message: String? = null) : Async<Nothing>
}

fun <T> Async<T>.dataOrNull(): T? = (this as? Async.Success<T>)?.data

inline fun <T, R> Async<T>.map(transform: (T) -> R): Async<R> = when (this) {
    is Async.Loading -> Async.Loading
    is Async.Success -> Async.Success(transform(data))
    is Async.Failure -> this
}

package io.github.feliperce.aabtoapk.data.remote

sealed class Resource<T, E>(
    val data: T? = null,
    val isLoading: Boolean = false,
    val error: E? = null
) {
    class Success<T, E>(data: T?) : Resource<T, E>(data)
    class Loading<T, E>(isLoading: Boolean) : Resource<T, E>(isLoading = isLoading)
    class Error<T, E>(error: E, data: T? = null) : Resource<T, E>(data = data, error = error)
}

package replace.the.packagename.model

sealed class DataState<out T> {
    data class Success<out T>(val data: T) : DataState<T>()
    class Error<T>(val message: String, val code: String, val data: T? = null,val isException: Boolean = false) : DataState<T>()
    data object Loading : DataState<Nothing>()
    data object Empty : DataState<Nothing>()
}
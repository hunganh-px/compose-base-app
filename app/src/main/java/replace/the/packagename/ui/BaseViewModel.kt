package replace.the.packagename.ui

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import replace.the.packagename.AppPreferences
import replace.the.packagename.BuildConfig
import retrofit2.HttpException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
abstract class BaseViewModel(open val app: Application) : AndroidViewModel(app) {
    protected val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    protected val _isError = MutableStateFlow<String?>(null)
    val isError = _isError.asStateFlow()

    protected val _errorData = MutableSharedFlow<MutableState<String?>>()
    val errorData = _errorData.asSharedFlow()

    var job: Job? = null

    var loaded = true

//    val sharedPreferences =
//        app.getSharedPreferences(app.resources.getString(R.string.app_name), Context.MODE_PRIVATE)

    protected fun launchJob(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
        errorHandler: CoroutineExceptionHandler = createErrorHandler()
    ): Job = viewModelScope.launch(context + errorHandler, start, block)


    protected fun launchLoadingJob(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit,
        errorHandler: CoroutineExceptionHandler = createErrorHandler()
    ): Job = viewModelScope.launch(context + errorHandler, start) {
        _isLoading.value = true
        try {
            block()
        } finally {
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }

    private fun createErrorHandler() =
        CoroutineExceptionHandler { _, throwable ->
            if (BuildConfig.DEBUG) {
                throwable.printStackTrace()
            }
            if (throwable !is CancellationException) {
//                if (withOutError) {

//                throwable.stackTrace
//                if (throwable is HttpException) {
//                    viewModelScope.launch {
//                        _errorData.emit(mutableStateOf(throwable.message))
//                    }
//                }
                when (throwable) {
                    is UnknownHostException -> {
                        Toast.makeText(app, "No internet connection", Toast.LENGTH_SHORT).show()
                    }

                    is HttpException -> {
                        viewModelScope.launch {
                            _errorData.emit(mutableStateOf(throwable.message))
                        }
                        Toast.makeText(
                            app,
                            "Yêu cầu không hơp lệ. Vui lòng kiểm tra và thử lại.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }

                    else -> {
                        Toast.makeText(
                            app,
                            "Có lỗi xảy ra. Vui lòng thử lại sau.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                _isError.value = throwable.message
//                } else {
//                    onError.postCall(throwable)
//                }
            }
        }

}
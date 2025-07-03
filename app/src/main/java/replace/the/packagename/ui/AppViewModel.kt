package replace.the.packagename.ui

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AppViewModel @Inject constructor(
    app: Application
) : BaseViewModel(app) {

    private val _networkState = MutableStateFlow(false)
    val networkState = _networkState.asStateFlow()

    fun setNetworkState(state: Boolean) {
        _networkState.value = state
    }

    val notiEnabled = mutableStateOf(NotificationManagerCompat.from(app).areNotificationsEnabled())

    fun setNotiState(granted: Boolean) {
        notiEnabled.value = granted
    }

}

val LocalAppViewModel = staticCompositionLocalOf<AppViewModel> {
    error("No GlobalViewModel provided")
}
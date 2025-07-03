package replace.the.packagename

import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import replace.the.packagename.ui.Destinations
import replace.the.packagename.ui.LocalAppViewModel
import replace.the.packagename.ui.MainScreen
import replace.the.packagename.ui.rememberAppNavController
import replace.the.packagename.ui.theme.AppTheme
import timber.log.Timber

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant()
        AppPreferences.init(this)
    }
}

@Composable
fun MainApp() {
    val appViewModel = LocalAppViewModel.current
    val networkState by appViewModel.networkState.collectAsStateWithLifecycle()

    AppTheme {
        val navController = rememberAppNavController()
        NavHost(
            navController = navController.navController,
            startDestination = Destinations.Main.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(route = Destinations.Main.route) {
                MainScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
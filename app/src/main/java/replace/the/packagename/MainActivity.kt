package replace.the.packagename

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import replace.the.packagename.ui.AppViewModel
import replace.the.packagename.ui.LocalAppViewModel
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private var connectivityManager: ConnectivityManager? = null
    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalAppViewModel provides appViewModel) {
                MainApp()
            }
        }
        initNetworkListener()
    }

    private fun initNetworkListener() {
        connectivityManager =
            getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager?
        connectivityManager?.let { connectivityManager ->
            if (connectivityManager.activeNetwork == null) {
                runOnUiThread {
                    appViewModel.setNetworkState(false)
                }
            }
            connectivityManager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    runOnUiThread {
                        Timber.d("NETWORK AVAILABLE")
                        appViewModel.setNetworkState(true)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    runOnUiThread {
                        appViewModel.setNetworkState(false)
                    }

                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    runOnUiThread {
                        appViewModel.setNetworkState(false)
                    }
                }
            })
        } ?: run {
            appViewModel.setNetworkState(false)
        }
    }
}

package replace.the.packagename.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Resources
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.core.view.WindowCompat
import kotlinx.coroutines.flow.distinctUntilChanged
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChangeStatusBarAmbient(pagerState: PagerState) {
    val view = LocalView.current
    val localFocus = LocalFocusManager.current
    val window = (view.context as Activity).window
    var selectedPage by remember {
        mutableIntStateOf(0)
    }
    val insetController = WindowCompat.getInsetsController(window, view)
    if (!view.isInEditMode) {
        LaunchedEffect(key1 = pagerState.currentPage) {
            localFocus.clearFocus()
            snapshotFlow { pagerState.currentPage }.distinctUntilChanged().collect {
                selectedPage = it
                val currentState = insetController.isAppearanceLightStatusBars
                val isLightRequest = selectedPage > 0
                if (isLightRequest != currentState) {
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                        isLightRequest
                }
            }
        }
    }
}

//
@Composable
fun ChangeStatusBarAmbient(lightScreen: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetController = WindowCompat.getInsetsController(window, view)
            val currentState = insetController.isAppearanceLightStatusBars
            if (lightScreen != currentState) {
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars =
                    lightScreen
            }
        }
    }
}

@Composable
fun ChangeNavigationBarAmbient(lightScreen: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars =
                lightScreen
        }
    }
}

@Stable
fun Dp.toPx(): Float = this.value * Resources.getSystem().displayMetrics.density


//fun setupRecurringWork(context: Context, gson: Gson, sharedPreferences: SharedPreferences) {
//    val appSettings =
//        gson.fromJson(
//            sharedPreferences.getString(Const.SharedPrefKeys.APP_SETTINGS, null),
//            AppSettings::class.java
//        )
//    val period =
//        Const.AppSettingsValues.AUTO_UPDATE_CHART_PERIOD_SECONDS[appSettings.autoUpdateChartPeriodSeconds]
//    Timber.d("WORKER APP SETTINGS $appSettings")
//    Timber.d("WORKER APP PERIOD $period")
//    val constraints = Constraints.Builder()
//        .setRequiredNetworkType(NetworkType.CONNECTED)
//        .build()
//
//    val workRequest = OneTimeWorkRequestBuilder<UpdateChannelWorker>()
//        .setConstraints(constraints)
//        .addTag(Const.UPDATE_WORKER_TAG)
//        .setInitialDelay(
//            period,
//            TimeUnit.SECONDS
//        ).build()
//
//    WorkManager.getInstance(context).enqueueUniqueWork(
//        Const.UPDATE_WORKER_TAG,
//        ExistingWorkPolicy.APPEND_OR_REPLACE, workRequest
//    )
//}

val LIST_CHARTS_COLOR = listOf(
    Color(0xFFb71c1c),
    Color(0xFFf57f17),
    Color(0xFF33691e),
    Color(0xFF0091ea),
    Color(0xFFbf360c),
    Color(0xFF01579b),
    Color(0xFFdd2c00),
    Color(0xFFe65100)
)

@Stable
fun Color.Companion.getChartsColor(index: Int) = LIST_CHARTS_COLOR[index]

fun generateRandomButtonColor() = run {
    Color.hsv(
        hue = Random.nextFloat() * 360f, saturation = Random.nextFloat(), value = .5f
    )

}

fun String.convertISODateToLongTime(): Long {
    return Instant.parse(this).toEpochMilli()
}

fun Float.formatToTime(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
        timeZone = TimeZone.getDefault()
    }
        .format(Date(this.toLong()))
}

fun String.formatISODateToDateTime(separator: String = " "): String {
    val instant = Instant.parse(this)
    val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy${separator}HH:mm", Locale.getDefault())
    return zonedDateTime.format(formatter)
}

fun Long.formatTimestampToDateTime(separator: String = " "): String {
    val instant = Instant.ofEpochMilli(this)
    val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
    val formatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy${separator}HH:mm", Locale.getDefault())
    return zonedDateTime.format(formatter)
}

fun Long?.formatTimestampToDate(): String? {
    return this?.let {
        val instant = Instant.ofEpochMilli(this)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }
}

fun Long?.formatAsDateParam(): String? {
    return this?.let {
        val instant = Instant.ofEpochMilli(this)
        val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return zonedDateTime.format(formatter)
    }
}

fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Permissions should be called in the context of an Activity")
}

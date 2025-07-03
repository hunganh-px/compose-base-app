package replace.the.packagename.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

sealed class Destinations(val route: String) {
    data object Main : Destinations("main")
}

/**
 * Remembers and creates an instance of [AppNavController]
 */
@Composable
fun rememberAppNavController(
    navController: NavHostController = rememberNavController()
): AppNavController = remember(navController) {
    AppNavController(navController)
}

/**
 * Responsible for holding UI Navigation logic.
 */
@Stable
class AppNavController(
    val navController: NavHostController,
) {

    // ----------------------------------------------------------
    // Navigation state source of truth
    // ----------------------------------------------------------

    val currentRoute: String?
        get() = navController.currentDestination?.route

    fun upPress() {
        navController.navigateUp()
    }

    fun navigateTo(route: String) {
        if (route != currentRoute) {
            navController.navigate(route) {
//                launchSingleTop = true
//                restoreState = true
//                // Pop up backstack to the first destination and save state. This makes going back
//                // to the start destination when pressing back in any other bottom tab.
//                popUpTo(findStartDestination(navController.graph).id) {
//                    saveState = true
//                }
            }
        }
    }

    fun popNavigateTo(route: String) {
        if (route != currentRoute) {
            navController.popBackStack(findStartDestination(navController.graph).id, true)
            navController.navigate(route) {
                launchSingleTop = true
//                restoreState = true
//                // Pop up backstack to the first destination and save state. This makes going back
//                // to the start destination when pressing back in any other bottom tab.
//                popUpTo(route)
            }
        }
    }

    fun popGatewayNavigateTo(route: String) {
        if (route != currentRoute) {
            navController.popBackStack(Destinations.Main.route, true)
            navController.navigate(route) {
                launchSingleTop = true
//                restoreState = true
//                // Pop up backstack to the first destination and save state. This makes going back
//                // to the start destination when pressing back in any other bottom tab.
//                popUpTo(route)
            }
        }
    }

}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

private val NavGraph.startDestination: NavDestination?
    get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
    return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
package com.example.localmessage.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.localmessage.ui.navigation.DestinationRoute

@Composable
fun rememberAppUIStateHolder(context: Context, navController: NavHostController = rememberNavController()): AppUIStateHolder {
    return remember(context, navController) {
        AppUIStateHolder(context, navController)
    }
}

class AppUIStateHolder(
    private val context: Context,
    val navController: NavHostController
) {

    val appOrientation = context.resources.configuration.orientation

    val currentPage = mutableStateOf(navController.currentBackStackEntry?.destination?.route ?: DestinationRoute.MAIN.name)

    fun navigateTo(destination: DestinationRoute) {
        currentPage.value = destination.name
        navController.navigate(destination.name) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }

}
package com.example.localmessage.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

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

    fun navigateTo(destination: String) {
        navController.navigate(destination) {
            popUpTo(0) {
                inclusive = true
            }
        }
    }

}
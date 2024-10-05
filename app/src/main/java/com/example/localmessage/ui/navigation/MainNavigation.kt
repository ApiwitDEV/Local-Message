package com.example.localmessage.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.localmessage.feature.history.HistoryScreen
import com.example.localmessage.feature.message.ui.MainScreen
import com.example.localmessage.ui.AppUIStateHolder

@Composable
fun MainNavigation(appUIStateHolder: AppUIStateHolder) {
    val navController = appUIStateHolder.navController
    NavHost(navController = navController, startDestination = DestinationRoute.MAIN.name) {
        composable(DestinationRoute.MAIN.name) { MainScreen(appUIStateHolder) }
        composable(DestinationRoute.HISTORY.name) { HistoryScreen() }
    }
}
package com.example.localmessage

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.localmessage.ui.navigation.DestinationRoute
import com.example.localmessage.ui.navigation.MainNavigation
import com.example.localmessage.ui.rememberAppUIStateHolder
import com.example.localmessage.ui.theme.LocalMessageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalMessageTheme {
                val context = LocalContext.current
                val appUIStateHolder = rememberAppUIStateHolder(context)
                when(appUIStateHolder.appOrientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        Scaffold(
                            bottomBar = {

                            }
                        ) { paddingValue ->
                            Box(
                                modifier = Modifier
                                .padding(
                                    top = paddingValue.calculateTopPadding(),
                                    bottom = paddingValue.calculateBottomPadding()
                                )
                            ) {
                                MainNavigation(appUIStateHolder)
                            }
                        }
                    }
                    Configuration.ORIENTATION_LANDSCAPE -> {
                        Row {
                            NavigationRail {
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    NavigationRailItem(
                                        selected = appUIStateHolder.currentPage.value == DestinationRoute.MAIN.name,
                                        onClick = { appUIStateHolder.navigateTo(DestinationRoute.MAIN) },
                                        icon = {
                                            Icon(painter = painterResource(id = R.drawable.baseline_message_24), contentDescription = null)
                                        }
                                    )
                                    NavigationRailItem(
                                        selected = appUIStateHolder.currentPage.value == DestinationRoute.HISTORY.name,
                                        onClick = { appUIStateHolder.navigateTo(DestinationRoute.HISTORY) },
                                        icon = {
                                            Icon(painter = painterResource(id = R.drawable.baseline_access_time_24), contentDescription = null)
                                        }
                                    )
                                }
                            }
                            MainNavigation(appUIStateHolder)
                        }
                    }
                }
            }
        }
    }
}
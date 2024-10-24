package com.example.localmessage

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localmessage.ui.AppViewModel
import com.example.localmessage.ui.navigation.DestinationRoute
import com.example.localmessage.ui.navigation.MainNavigation
import com.example.localmessage.ui.rememberAppUIStateHolder
import com.example.localmessage.ui.theme.LocalMessageTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val appViewModel by viewModel<AppViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LocalMessageTheme {
                val context = LocalContext.current
                val focusManager = LocalFocusManager.current
                val appUIStateHolder = rememberAppUIStateHolder(context)
                LaunchedEffect(null) {
                    appViewModel.currentPage.collectLatest { destination ->
                        appUIStateHolder.navigateTo(destination)
                    }
                }
                when(appUIStateHolder.appOrientation) {
                    Configuration.ORIENTATION_PORTRAIT -> {
                        Scaffold(
                            modifier = Modifier.clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { focusManager.clearFocus() },
                            bottomBar = {
                                NavigationBar(
                                    modifier = Modifier.fillMaxHeight(0.125f)
                                ) {
                                    NavigationBarItem(
                                        modifier = Modifier.fillMaxHeight(1f),
                                        selected = appViewModel.currentPage.collectAsStateWithLifecycle().value == DestinationRoute.MAIN.name,
                                        onClick = { appViewModel.updateCurrentPage(DestinationRoute.MAIN) },
                                        icon = {
                                            Icon(painter = painterResource(id = R.drawable.baseline_message_24), contentDescription = null)
                                        }
                                    )
                                    NavigationBarItem(
                                        modifier = Modifier.fillMaxHeight(1f),
                                        selected = appViewModel.currentPage.collectAsStateWithLifecycle().value == DestinationRoute.HISTORY.name,
                                        onClick = { appViewModel.updateCurrentPage(DestinationRoute.HISTORY) },
                                        icon = {
                                            Icon(painter = painterResource(id = R.drawable.baseline_assignment_24), contentDescription = null)
                                        }
                                    )
                                }
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
                        Row(modifier = Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { focusManager.clearFocus() }) {
                            NavigationRail {
                                Column(
                                    modifier = Modifier.fillMaxHeight(),
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    NavigationRailItem(
                                        selected = appViewModel.currentPage.collectAsStateWithLifecycle().value == DestinationRoute.MAIN.name,
                                        onClick = { appViewModel.updateCurrentPage(DestinationRoute.MAIN) },
                                        icon = {
                                            Icon(painter = painterResource(id = R.drawable.baseline_message_24), contentDescription = null)
                                        }
                                    )
                                    NavigationRailItem(
                                        selected = appViewModel.currentPage.collectAsStateWithLifecycle().value == DestinationRoute.HISTORY.name,
                                        onClick = { appViewModel.updateCurrentPage(DestinationRoute.HISTORY) },
                                        icon = {
                                            Icon(painter = painterResource(id = R.drawable.baseline_assignment_24), contentDescription = null)
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
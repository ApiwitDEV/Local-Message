package com.example.localmessage.ui

import androidx.lifecycle.ViewModel
import com.example.localmessage.ui.navigation.DestinationRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel: ViewModel() {

    private val _currentPage = MutableStateFlow(DestinationRoute.MAIN.name)
    val currentPage = _currentPage.asStateFlow()

    fun updateCurrentPage(destinationRoute: DestinationRoute) {
        _currentPage.value = destinationRoute.name
    }

}
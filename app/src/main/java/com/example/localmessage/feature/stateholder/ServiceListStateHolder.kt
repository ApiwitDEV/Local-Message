package com.example.localmessage.feature.stateholder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.localmessage.feature.uistatemodel.NSDServiceItemUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext

@Composable
fun rememberServiceListState(
    serviceList: List<NSDServiceItemUIState>,
    scope: CoroutineScope,
    onSelectService: (String) -> Unit
): ServiceListStateHolder {
    return remember(serviceList, scope, onSelectService) {
        ServiceListStateHolder(serviceList, scope, onSelectService)
    }
}

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
class ServiceListStateHolder(
    private val originalServiceList: List<NSDServiceItemUIState>,
    private val scope: CoroutineScope,
    private val onSelectService: (String) -> Unit
) {

    private val _serviceList = MutableStateFlow(originalServiceList)
    val uiState = _serviceList.asStateFlow()

    fun onItemClicked(domainName: String, ipAddress: String) {
        scope.launch(newSingleThreadContext("")) {
            _serviceList.value = originalServiceList.map { item ->
                item.copy(isSelect = item.domainName == domainName)
            }
        }
        onSelectService(ipAddress)
    }

}
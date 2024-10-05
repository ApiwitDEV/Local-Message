package com.example.localmessage.feature.stateholder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Composable
fun rememberServiceActionState(
    scope: CoroutineScope,
    onRequestClick: (String) -> Unit,
): ServiceActionStateHolder {
    return remember(scope) {
        ServiceActionStateHolder(
            scope = scope,
            onRequestClick = onRequestClick
        )
    }
}

class ServiceActionStateHolder(
    private val scope: CoroutineScope,
    private val onRequestClick: (String) -> Unit
) {

    private val _uiState = MutableStateFlow("")
    val uiState = _uiState.asStateFlow()

    fun setText(text: String) {
        _uiState.value = text
    }

    fun testRequest() {
        onRequestClick(_uiState.value)
        _uiState.value = ""
    }

}
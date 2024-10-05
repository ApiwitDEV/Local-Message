package com.example.localmessage.feature.message.stateholder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.localmessage.feature.message.uistatemodel.HistoryItemUIState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberHistoryState(history: StateFlow<List<HistoryItemUIState>>): HistoryStateHolder {
    return remember(history) {
        HistoryStateHolder(history)
    }
}

class HistoryStateHolder(
    val history: StateFlow<List<HistoryItemUIState>>
) {

}
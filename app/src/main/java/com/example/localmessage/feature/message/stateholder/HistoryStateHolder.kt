package com.example.localmessage.feature.message.stateholder

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.example.localmessage.feature.message.uistatemodel.ChatItemUIState
import kotlinx.coroutines.flow.StateFlow

@Composable
fun rememberChatListStateHolder(history: StateFlow<List<ChatItemUIState>>): HistoryStateHolder {
    return remember(history) {
        HistoryStateHolder(history)
    }
}

class HistoryStateHolder(
    val history: StateFlow<List<ChatItemUIState>>
) {

}
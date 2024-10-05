package com.example.localmessage.feature.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localmessage.feature.stateholder.HistoryStateHolder
import com.example.localmessage.feature.stateholder.MainViewModel
import com.example.localmessage.feature.stateholder.ServiceActionStateHolder
import com.example.localmessage.feature.stateholder.ServiceListStateHolder
import com.example.localmessage.feature.stateholder.rememberHistoryState
import com.example.localmessage.feature.stateholder.rememberServiceActionState
import com.example.localmessage.feature.stateholder.rememberServiceListState
import com.example.localmessage.feature.uistatemodel.HistoryItemUIState
import com.example.localmessage.feature.uistatemodel.NSDServiceItemUIState
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(viewModel: MainViewModel = koinViewModel()) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = null) {
        viewModel.findLocalNetworkService()
    }
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val serviceList = viewModel.serviceList.collectAsStateWithLifecycle().value
        val serviceListState = rememberServiceListState(
            serviceList = serviceList,
            scope = scope,
            onSelectService = {
                viewModel.selectService(it)
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ServiceList(
                serviceListStateHolder = serviceListState,
                modifier = Modifier.weight(1f)
            )
            HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.scrim)
            ServiceDetail(
                modifier = Modifier.weight(1f),
                onRequestClick = {
                    viewModel.testRequest(it)
                },
                history = viewModel.historyList
            )
        }
    }
}

@Composable
private fun ServiceList(
    serviceListStateHolder: ServiceListStateHolder,
    modifier: Modifier
) {
    val uiState = serviceListStateHolder.uiState.collectAsStateWithLifecycle().value
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(uiState) {
            ServiceItem(
                item = it,
                onClick = { domainName, ipAddress ->
                    serviceListStateHolder.onItemClicked(domainName, ipAddress)
                }
            )
        }
    }
}

@Composable
private fun ServiceItem(item: NSDServiceItemUIState, onClick: (String, String) -> Unit) {
    Column(
        modifier = Modifier
            .clickable {
                onClick(item.domainName, item.ipAddress)
            }
            .background(
                color = if (item.isSelect) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(text = "Domain Name : ${item.domainName}")
        Text(text = "IP Address : ${item.ipAddress}")
    }
}

@Composable
private fun ServiceDetail(
    modifier: Modifier,
    onRequestClick: (String) -> Unit,
    history: StateFlow<List<HistoryItemUIState>>
) {
    val scope = rememberCoroutineScope()
    val historyState = rememberHistoryState(history)
    val serviceActionState = rememberServiceActionState(
        scope = scope,
        onRequestClick = onRequestClick
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        History(historyState)
        ServiceAction(serviceActionStateHolder = serviceActionState)
    }
}

@Composable
private fun ServiceAction(serviceActionStateHolder: ServiceActionStateHolder) {
    val uiState = serviceActionStateHolder.uiState.collectAsStateWithLifecycle().value
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(0.75f),
            value = uiState,
            onValueChange = serviceActionStateHolder::setText,
            placeholder = {
                Text(text = "message")
            }
        )
        Button(
            modifier = Modifier.weight(0.2f),
            onClick = { serviceActionStateHolder.testRequest() }
        ) {
            Text(text = "Send Request")
        }
    }
}

@Composable
private fun History(
    historyStateHolder: HistoryStateHolder
) {
    val list = historyStateHolder.history.collectAsStateWithLifecycle().value
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(list) {
            HistoryItem(item = it)
        }
    }
}

@Composable
private fun HistoryItem(item: HistoryItemUIState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = if (item.type == "response") Arrangement.Start else Arrangement.End
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.75f)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = item.ipAddress)
                Text(
                    text = if (item.type == "response") {
                        item.responseMessage
                    }
                    else {
                        item.requestMessage
                    }
                )
            }
        }
    }
}
//
//@Preview(showBackground = true)
//@Composable
//fun ScreenPreview() {
//    LocalNetworkTheme {
//        Screen()
//    }
//}
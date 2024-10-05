package com.example.localmessage.feature.message.ui

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localmessage.feature.message.stateholder.HistoryStateHolder
import com.example.localmessage.feature.message.stateholder.HomeViewModel
import com.example.localmessage.feature.message.stateholder.ServiceActionStateHolder
import com.example.localmessage.feature.message.stateholder.ServiceListStateHolder
import com.example.localmessage.feature.message.stateholder.rememberHistoryState
import com.example.localmessage.feature.message.stateholder.rememberServiceActionState
import com.example.localmessage.feature.message.stateholder.rememberServiceListState
import com.example.localmessage.feature.message.uistatemodel.HistoryItemUIState
import com.example.localmessage.feature.message.uistatemodel.NSDServiceItemUIState
import com.example.localmessage.ui.AppUIStateHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    appStateHolder: AppUIStateHolder,
    viewModel: HomeViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = null) {
        viewModel.findLocalNetworkService()
    }

    when(appStateHolder.appOrientation) {
        Configuration.ORIENTATION_LANDSCAPE -> LandscapeContent(
            scope = scope,
            serviceList = viewModel.serviceList.collectAsStateWithLifecycle().value,
            historyList = viewModel.historyList,
            onSelectService = {
                viewModel.selectService(it)
            },
            onRequestClick = {
                viewModel.testRequest(it)
            }
        )
        Configuration.ORIENTATION_PORTRAIT -> PortraitContent(
            scope = scope,
            serviceList = viewModel.serviceList.collectAsStateWithLifecycle().value,
            historyList = viewModel.historyList,
            onSelectService = {
                viewModel.selectService(it)
            },
            onRequestClick = {
                viewModel.testRequest(it)
            }
        )
    }

}

@Composable
private fun LandscapeContent(
    scope: CoroutineScope,
    serviceList: List<NSDServiceItemUIState>,
    historyList: StateFlow<List<HistoryItemUIState>>,
    onSelectService: (String) -> Unit,
    onRequestClick: (String) -> Unit
) {
    val density = LocalDensity.current
    var pageWidth by remember { mutableStateOf(0.dp) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    Scaffold(
        modifier = Modifier
            .onSizeChanged { x ->
                pageWidth = with(density) {
                    x.width.toDp()
                }
                offsetX = pageWidth.value
            }
            .fillMaxSize()
    ) { innerPadding ->
        val serviceListState = rememberServiceListState(
            serviceList = serviceList,
            scope = scope,
            onSelectService = onSelectService
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.width(with(density){offsetX.toDp()})) {
                ServiceList(serviceListStateHolder = serviceListState)
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.Center)
                        .background(Color.LightGray)
                        .width(4.dp)
                ){}
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxHeight(0.2f)
                        .width(24.dp)
                        .align(Alignment.Center)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                Log.d("bas test", dragAmount.x.toString())
                                offsetX += dragAmount.x
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                ){}
            }
            ServiceDetail(
                modifier = Modifier.width(pageWidth - with(density){offsetX.toDp()}),
                onRequestClick = onRequestClick,
                history = historyList
            )
        }
    }
}

@Composable
private fun PortraitContent(
    scope: CoroutineScope,
    serviceList: List<NSDServiceItemUIState>,
    historyList: StateFlow<List<HistoryItemUIState>>,
    onSelectService: (String) -> Unit,
    onRequestClick: (String) -> Unit
) {
    val density = LocalDensity.current
    var pageHeight by remember { mutableStateOf(0.dp) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    Scaffold(
        modifier = Modifier
            .onSizeChanged { value ->
                pageHeight = with(density) {
                    value.height.toDp()
                }
                offsetY = pageHeight.value
            }
            .fillMaxSize()
    ) { innerPadding ->
        val serviceListState = rememberServiceListState(
            serviceList = serviceList,
            scope = scope,
            onSelectService = onSelectService
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(modifier = Modifier.height(with(density){offsetY.toDp()})) {
                ServiceList(serviceListStateHolder = serviceListState)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .background(Color.LightGray)
                        .height(4.dp)
                ){}
                Card(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth(0.2f)
                        .height(24.dp)
                        .align(Alignment.Center)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                Log.d("bas test", dragAmount.y.toString())
                                offsetY += dragAmount.y
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color.LightGray),
                ){}
            }
            ServiceDetail(
                modifier = Modifier.height(pageHeight - with(density){offsetY.toDp()}),
                onRequestClick = onRequestClick,
                history = historyList
            )
        }
    }
}

@Composable
private fun ServiceList(
    serviceListStateHolder: ServiceListStateHolder,
) {
    val uiState = serviceListStateHolder.uiState.collectAsStateWithLifecycle().value
    LazyColumn(horizontalAlignment = Alignment.CenterHorizontally) {
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
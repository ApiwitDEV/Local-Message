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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.localmessage.R
import com.example.localmessage.feature.message.stateholder.HistoryStateHolder
import com.example.localmessage.feature.message.stateholder.MessageViewModel
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
fun MessageScreen(
    appStateHolder: AppUIStateHolder,
    viewModel: MessageViewModel = koinViewModel()
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(key1 = null) {
        viewModel.findLocalNetworkService()
        viewModel.subscribeMessageFromOther()
    }

    when(appStateHolder.appOrientation) {
        Configuration.ORIENTATION_LANDSCAPE -> LandscapeContent(
            scope = scope,
            serviceList = viewModel.serviceList.collectAsStateWithLifecycle().value,
            historyList = viewModel.chatList,
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
            chatList = viewModel.chatList,
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
    val serviceListState = rememberServiceListState(
        serviceList = serviceList,
        scope = scope,
        onSelectService = {
            onSelectService(it)
        }
    )
    Row(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .systemBarsPadding()
            .navigationBarsPadding()
            .padding(vertical = 16.dp)
            .onSizeChanged { value ->
                pageWidth = with(density) {
                    value.width.toDp()
                }
                offsetX = pageWidth.value
            }
            .fillMaxSize()
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
        serviceListState.selectedService.collectAsStateWithLifecycle().value?.let {
            Box(modifier = Modifier.width(pageWidth - with(density){offsetX.toDp()})) {
                ServiceDetail(
                    selectedService = it,
                    onRequestClick = onRequestClick,
                    chatList = historyList
                )
            }
        }
    }
}

@Composable
private fun PortraitContent(
    scope: CoroutineScope,
    serviceList: List<NSDServiceItemUIState>,
    chatList: StateFlow<List<HistoryItemUIState>>,
    onSelectService: (String) -> Unit,
    onRequestClick: (String) -> Unit
) {
    val density = LocalDensity.current
    var pageHeight by remember { mutableStateOf(0.dp) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val serviceListState = rememberServiceListState(
        serviceList = serviceList,
        scope = scope,
        onSelectService = onSelectService
    )
    Column(
        modifier = Modifier
            .padding(vertical = 16.dp)
            .onSizeChanged { value ->
                pageHeight = with(density) {
                    value.height.toDp()
                }
                offsetY = pageHeight.value
            }
            .fillMaxSize()
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
        serviceListState.selectedService.collectAsStateWithLifecycle().value?.let {
            Box(modifier = Modifier.height(pageHeight - with(density){offsetY.toDp()})) {
                ServiceDetail(
                    selectedService = it,
                    onRequestClick = onRequestClick,
                    chatList = chatList
                )
            }
        }
    }
}

@Composable
private fun ServiceList(
    serviceListStateHolder: ServiceListStateHolder,
) {
    val uiState = serviceListStateHolder.uiState.collectAsStateWithLifecycle().value
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ServiceDetail(
    selectedService: NSDServiceItemUIState,
    onRequestClick: (String) -> Unit,
    chatList: StateFlow<List<HistoryItemUIState>>
) {
    val scope = rememberCoroutineScope()
    val historyState = rememberHistoryState(chatList)
    val serviceActionState = rememberServiceActionState(
        scope = scope,
        onRequestClick = onRequestClick
    )
    var isShowDropdown by remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = selectedService.domainName,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = selectedService.ipAddress,
                                fontSize = 20.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { isShowDropdown = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_more_vert_24),
                                contentDescription = null
                            )
                        }
                        DropdownMenu(expanded = isShowDropdown, onDismissRequest = { isShowDropdown = false }) {
                            DropdownMenuItem(
                                text = {
                                    Text(text = "Clear Chat")
                                },
                                onClick = {

                                }
                            )
                        }
                    }
                )
                HorizontalDivider()
            }
        },
        bottomBar = {
            HorizontalDivider()
            ServiceAction(serviceActionStateHolder = serviceActionState)
        }
    ) {
        Box(modifier = Modifier.padding(top = it.calculateTopPadding(), bottom = it.calculateBottomPadding())) {
            History(historyState)
        }
    }
}

@Composable
private fun ServiceAction(serviceActionStateHolder: ServiceActionStateHolder) {
    val uiState = serviceActionStateHolder.uiState.collectAsStateWithLifecycle().value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        IconButton(
            modifier = Modifier.scale(1.5f),
            onClick = { /*TODO*/ }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_image_24),
                contentDescription = "pick image",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            value = uiState,
            onValueChange = serviceActionStateHolder::setText,
            shape = RoundedCornerShape(25),
            label = {
                Text(text = "Message")
            }
        )
        Spacer(modifier = Modifier.width(16.dp))
        IconButton(
            modifier = Modifier
                .scale(1.5f),
            onClick = serviceActionStateHolder::testRequest
        ) {
            Icon(
                modifier = Modifier.rotate(90f),
                painter = painterResource(id = R.drawable.baseline_navigation_24),
                contentDescription = "send request",
                tint = MaterialTheme.colorScheme.primary
            )
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
        horizontalArrangement = if (item.type == "from_other") Arrangement.Start else Arrangement.End
    ) {
        Card {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                Text(text = item.sender)
                Text(text = item.message)
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
package com.example.localmessage.feature.message.stateholder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.onFailure
import com.example.data.onSuccess
import com.example.data.repository.NSDRepository
import com.example.data.repository.TestRepository
import com.example.localmessage.feature.message.uistatemodel.HistoryItemUIState
import com.example.localmessage.feature.message.uistatemodel.NSDServiceItemUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val nsdRepository: NSDRepository,
    private val testRepository: TestRepository
): ViewModel() {

    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    private val _serviceList = MutableStateFlow(listOf<NSDServiceItemUIState>())
    val serviceList = _serviceList.asStateFlow()

    private val _historyList = MutableStateFlow(listOf<HistoryItemUIState>())
    val historyList = _historyList.asStateFlow()

    private var _selectedReceiver = ""

    fun findLocalNetworkService() {
        viewModelScope.launch(Dispatchers.IO) {
            nsdRepository.findLocalNetworkService()
                .map { localNetworkList ->
                    localNetworkList.map { item ->
                        NSDServiceItemUIState(
                            domainName = item.domainName,
                            ipAddress = item.ipAddress,
                            isSelect = false
                        )
                    }
                }
                .collectLatest {
                    _serviceList.value = it
                }
        }
    }

    fun testRequest(message: String) {
        viewModelScope.launch(Dispatchers.IO) {
            testRepository.sendRequestByAddress(message)
                .forEach { response ->
                    response
                        .onSuccess {
                            _response.value = it
                            _historyList.update {
                                val x = _historyList.value.toMutableList()
                                x.add(
                                    HistoryItemUIState(
                                        domainName = "",
                                        ipAddress = "you",
                                        responseMessage = "",
                                        requestMessage = message,
                                        sender = "",
                                        type = "request"
                                    )
                                )
                                x
                            }
                        }
                        .onFailure {
                            _response.value = it.message.toString()
                        }
                }
        }
    }

    fun selectService(ipAddress: String) {
        _selectedReceiver = ipAddress
        testRepository.setIpAddressToSend(listOf(ipAddress))
    }

//    fun subscribeResponse() {
//        viewModelScope.launch(Dispatchers.IO) {
//            testRepository.subscribeResponse()
//                .collectLatest { response ->
//                    _historyList.update {
//                        val x = _historyList.value.toMutableList()
//                        x.add(
//                            HistoryItemUIState(
//                                domainName = "",
//                                ipAddress = _selectedReceiver,
//                                responseMessage = response,
//                                requestMessage = "",
//                                sender = "",
//                                type = "response"
//                            )
//                        )
//                        x
//                    }
//                }
//        }
//    }

}
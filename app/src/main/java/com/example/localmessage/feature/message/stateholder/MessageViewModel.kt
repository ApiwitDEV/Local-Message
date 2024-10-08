package com.example.localmessage.feature.message.stateholder

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.onFailure
import com.example.data.onSuccess
import com.example.data.repository.NSDRepository
import com.example.data.repository.ServerRepository
import com.example.data.repository.TestRepository
import com.example.localmessage.feature.message.uistatemodel.HistoryItemUIState
import com.example.localmessage.feature.message.uistatemodel.NSDServiceItemUIState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MessageViewModel(
    private val nsdRepository: NSDRepository,
    private val testRepository: TestRepository,
    private val serverRepository: ServerRepository
): ViewModel() {

    private val _serviceList = MutableStateFlow(listOf<NSDServiceItemUIState>())
    val serviceList = _serviceList.asStateFlow()

    private val _chatList = MutableStateFlow(listOf<HistoryItemUIState>())
    val chatList = _chatList.asStateFlow()

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
                            _chatList.update {
                                val newHistoryList = _chatList.value.toMutableList()
                                newHistoryList.add(
                                    HistoryItemUIState(
                                        domainName = "",
                                        ipAddress = "",
                                        message = message,
                                        sender = "you",
                                        type = "to_other"
                                    )
                                )
                                newHistoryList
                            }
                        }
                        .onFailure {
                        }
                }
        }
    }

    fun selectService(ipAddress: String) {
        testRepository.setIpAddressToSend(listOf(ipAddress))
    }

    fun subscribeMessageFromOther() {
        viewModelScope.launch(Dispatchers.IO) {
            serverRepository.subscribeMessageFromOther()
                .collect { message ->
                    Log.d("bas test", message.toString())
                    _chatList.update {
                        val newHistoryList = _chatList.value.toMutableList()
                        newHistoryList.add(
                            HistoryItemUIState(
                                domainName = "",
                                ipAddress = "",
                                message = message.test,
                                sender = message.sender,
                                type = "from_other"
                            )
                        )
                        newHistoryList
                    }
                }
        }
    }

}
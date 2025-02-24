package com.example.localmessage.feature.message.stateholder

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.repository.NSDRepository
import com.example.data.repository.ServerRepository
import com.example.data.repository.MessageRepository
import com.example.data.byteArrayToBitmap
import com.example.localmessage.feature.message.uistatemodel.ChatItemUIState
import com.example.localmessage.feature.message.uistatemodel.NSDServiceItemUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MessageViewModel @Inject constructor(
    nsdRepository: NSDRepository,
    private val messageRepository: MessageRepository,
    private val serverRepository: ServerRepository
): ViewModel() {

    val serviceList = nsdRepository.findLocalNetworkService()
        .map { localNetworkList ->
            localNetworkList.map { item ->
                NSDServiceItemUIState(
                    domainName = item.domainName,
                    ipAddress = item.ipAddress,
                    isSelect = false
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf()
        )

    private val _chatList = MutableStateFlow(listOf<ChatItemUIState>())
    val chatList = _chatList.asStateFlow()

    private val queueChannel = Channel<Pair<String?, Bitmap?>>(UNLIMITED)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            queueChannel.consumeAsFlow().collect {
                val message = it.first
                val image = it.second
                if (image != null) {
                    messageRepository.sendRequestWithFileByAddress(
                        message = message?:"",
                        image = image
                    )
                }
                else {
                    messageRepository.sendRequestByAddress(message?:"")
                }
            }
        }
    }

    fun sendMessage(message: String, image: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            queueChannel.send(Pair(message, image))
        }
    }

    fun selectService(ipAddress: String) {
        messageRepository.setIpAddressToSend(ipAddress)
    }

    fun subscribeMessageFromOther() {
        viewModelScope.launch(Dispatchers.IO) {
            serverRepository.subscribeMessageFromOther()
                .collect { rawData ->
                    rawData.fileBytes
                    val message = rawData.testRequestBody
                    Log.d("bas test", message.toString())
                    _chatList.update {
                        val newChatList = _chatList.value.toMutableList()
                        newChatList.add(
                            ChatItemUIState(
                                id = null,
                                message = message?.test?:"",
                                sender = message?.sender?:"",
                                type = "from_other",
                                image = rawData.fileBytes?.byteArrayToBitmap(),
                                progress = null
                            )
                        )
                        newChatList
                    }
                }
        }
    }

    fun subscribeMessageFromMe() {
        viewModelScope.launch(Dispatchers.IO) {
            messageRepository.messageFlow.collectLatest { message ->
                _chatList.update {
                    Log.d("bas test", "${message.progress}")
                    val newChatList = _chatList.value.toMutableList()
                    if (newChatList.find { it.id == message.id } == null) {
                        newChatList.add(
                            ChatItemUIState(
                                id = message.id,
                                message = message.message,
                                sender = "you",
                                type = "to_other",
                                image = message.image,
                                progress = message.progress
                            )
                        )
                    }
                    else {
                        newChatList.replaceAll {
                            if (it.id == message.id) {
                                it.copy(progress = message.progress)
                            }
                            else {
                                it
                            }
                        }
                    }
                    newChatList
                }
            }
        }
    }

}
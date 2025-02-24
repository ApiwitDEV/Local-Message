package com.example.data.repository

import android.graphics.Bitmap
import com.example.data.ResultData
import com.example.data.bitmapToByteArray
import com.example.data.datasource.network.local.client.LocalService
import com.example.data.datasource.network.local.client.NSD
import com.example.data.datasource.network.local.model.test.TestRequestBody
import com.example.data.datasource.ondevice.database.chathistory.ChatDao
import io.ktor.http.HttpMethod
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val localService: LocalService,
    private val nsd: NSD,
    private val chatDao: ChatDao
) {

    data class Message(
        val id: Int,
        val progress: Double,
        val message: String?,
        val image: Bitmap?
    )

    private var id = 0

//    private val ipAddressToSend = mutableListOf<String>()
//
//    fun getIpAddressList() = ipAddressToSend

    private var ipAddressToSend: String? = null

//    fun setIpAddressToSend(list: List<String>) {
//        ipAddressToSend.clear()
//        ipAddressToSend.addAll(list)
//    }

    private val _messageFlow = MutableSharedFlow<Message>(replay = 1)
    val messageFlow = _messageFlow.asSharedFlow()

    fun setIpAddressToSend(ipAddress: String) {
        ipAddressToSend = ipAddress
    }

    suspend fun sendRequestByAddress(message: String): ResultData<String> {
        id += 1
        _messageFlow.tryEmit(
            Message(
                id = id,
                progress = 0.0,
                message = message,
                image = null
            )
        )
        return localService.sendRequestByAddress(
            httpMethod = HttpMethod.Post,
            endPoint = "http://${ipAddressToSend}:8888/test",
            queryParameters = arrayOf(),
            requestBody = TestRequestBody(
                test = message,
                sender = nsd.myServiceName
            ),
            onUpdateProgress = { progress ->
                _messageFlow.tryEmit(
                    Message(
                        id = id,
                        progress = progress,
                        message = message,
                        image = null
                    )
                )
            }
        )
    }

    suspend fun sendRequestWithFileByAddress(message: String, image: Bitmap) {
        id += 1
        _messageFlow.tryEmit(
            Message(
                id = id,
                progress = 0.0,
                message = message,
                image = image
            )
        )
        localService.sendMultiPartRequestByAddress(
            httpMethod = HttpMethod.Post,
            endPoint = "http://${ipAddressToSend}:8888/test",
            requestBody = TestRequestBody(
                test = message,
                sender = nsd.myServiceName
            ),
            imageByteArray = image.bitmapToByteArray(),
            onUpdateProgress = { progress ->
                _messageFlow.tryEmit(
                    Message(
                        id = id,
                        progress = progress,
                        message = message,
                        image = image
                    )
                )
            }
        )
    }

}
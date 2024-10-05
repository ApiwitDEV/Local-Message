package com.example.data.repository

import com.example.data.ResultData
import com.example.data.datasource.network.local.client.LocalService
import com.example.data.datasource.network.local.model.test.TestRequestBody
import com.example.data.datasource.network.local.server.LocalServer
import io.ktor.http.HttpMethod

class TestRepository(
    private val localService: LocalService,
    private val localServer: LocalServer
) {

    private val ipAddressToSend = mutableListOf<String>()

    suspend fun testRequest() {
        localService.testRequest(
            httpMethod = HttpMethod.Post,
            path = "/test",
            queryParameters = arrayOf(),
            requestBody = TestRequestBody(
                test = "test",
                sender = "sender: Bas"
            )
        )
    }

    fun getIpAddressList() = ipAddressToSend

    fun setIpAddressToSend(list: List<String>) {
        ipAddressToSend.clear()
        ipAddressToSend.addAll(list)
    }

    suspend fun sendRequestByAddress(message: String): List<ResultData<String>> {
        return ipAddressToSend.map {
            localService.sendRequestByAddress(
                httpMethod = HttpMethod.Post,
                endPoint = "http://${it}:8888/test",
                queryParameters = arrayOf(),
                requestBody = TestRequestBody(
                    test = message,
                    sender = ""
                )
            )
        }
    }

}
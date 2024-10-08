package com.example.data.repository

import com.example.data.ResultData
import com.example.data.datasource.network.local.client.LocalService
import com.example.data.datasource.network.local.client.NSD
import com.example.data.datasource.network.local.model.test.TestRequestBody
import io.ktor.http.HttpMethod

class TestRepository(private val localService: LocalService, private val nsd: NSD) {

    private val ipAddressToSend = mutableListOf<String>()

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
                    sender = nsd.myServiceName
                )
            )
        }
    }

}
package com.example.data.datasource.network.local.client

import com.example.data.Failure
import com.example.data.ResultData
import com.example.data.Success
import com.example.data.datasource.network.local.DNSBaseService
import com.example.data.datasource.network.local.model.test.TestRequestBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.plugin
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class LocalService(private val dnsBaseService: DNSBaseService) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
    }.apply {
        plugin(HttpSend).intercept { request ->
            val originalCall = execute(request)
            if (originalCall.response.status.value !in 100..399) {
                execute(request)
            } else {
                originalCall
            }
        }
    }

    suspend fun testRequest(
        httpMethod: HttpMethod,
        path: String,
        vararg queryParameters: Pair<String,String>,
        requestBody: TestRequestBody
    ): List<ResultData<String>> {
        return runBlocking(Dispatchers.IO) {
            dnsBaseService.getIpAddressList().fold(mutableListOf<Deferred<ResultData<String>>>()) { acc, address ->
                acc.add(
                    async {
                        makeRequest(
                            httpMethod = httpMethod,
                            endPoint = address + path,
                            queryParameters = queryParameters.toList(),
                            requestBody = requestBody
                        )
                    }
                )
                acc
            }
                .map {
                    it.await()
                }
        }
    }

    suspend fun sendRequestByAddress(
        httpMethod: HttpMethod,
        endPoint: String,
        vararg queryParameters: Pair<String, String>,
        requestBody: TestRequestBody
    ): ResultData<String> {
        return runBlocking(Dispatchers.IO) {
            makeRequest(
                httpMethod = httpMethod,
                endPoint = endPoint,
                queryParameters = queryParameters.toList(),
                requestBody = requestBody
            )
        }
    }

    private suspend inline fun <reified T> makeRequest(
        httpMethod: HttpMethod,
        endPoint: String,
        queryParameters: List<Pair<String,String>>,
        requestBody: T
    ): ResultData<String> {
        return try {
            val request = client.request(endPoint) {
                contentType(ContentType.Application.Json)
                method = httpMethod
                queryParameters.forEach {
                    parameter(it.first,it.second)
                }
                setBody(requestBody)
            }
            val body = request.body<String>()
            Success(body)
        }
        catch (e: Exception) {
            Failure(e)
        }
    }

}
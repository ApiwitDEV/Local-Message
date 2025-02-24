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
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.plugin
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalService @Inject constructor(private val dnsBaseService: DNSBaseService) {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json()
        }
        install(Logging) {
            logger = Logger.ANDROID
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 1000
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
        return withContext(Dispatchers.IO) {
            dnsBaseService.getIpAddressList().fold(mutableListOf<Deferred<ResultData<String>>>()) { acc, address ->
                acc.add(
                    async {
                        makeRequest(
                            httpMethod = httpMethod,
                            endPoint = address + path,
                            queryParameters = queryParameters.toList(),
                            requestBody = requestBody,
                            onUpdateProgress = {}
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
        requestBody: TestRequestBody,
        onUpdateProgress: (Double) -> Unit
    ): ResultData<String> {
        return withContext(Dispatchers.IO) {
            makeRequest(
                httpMethod = httpMethod,
                endPoint = endPoint,
                queryParameters = queryParameters.toList(),
                requestBody = requestBody,
                onUpdateProgress = onUpdateProgress
            )
        }
    }

    suspend fun sendMultiPartRequestByAddress(
        httpMethod: HttpMethod,
        endPoint: String,
        vararg queryParameters: Pair<String, String>,
        requestBody: TestRequestBody,
        imageByteArray: ByteArray,
        onUpdateProgress: (Double) -> Unit
    ) {
        return withContext(Dispatchers.IO) {
            makeMultiPartRequest(
                httpMethod = httpMethod,
                endPoint = endPoint,
                requestBody = requestBody,
                byteArray = imageByteArray,
                onUpdateProgress = onUpdateProgress
            )
        }
    }

    private suspend inline fun <reified T> makeRequest(
        httpMethod: HttpMethod,
        endPoint: String,
        queryParameters: List<Pair<String,String>>,
        requestBody: T,
        crossinline onUpdateProgress: (Double) -> Unit
    ): ResultData<String> {
        return try {
            val request = client.request(endPoint) {
                contentType(ContentType.Application.Json)
                method = httpMethod
                queryParameters.forEach {
                    parameter(it.first,it.second)
                }
                setBody(requestBody)
                onUpload { bytesSentTotal, contentLength ->
                    val progressPercentage = 100.0 * bytesSentTotal/contentLength
                    onUpdateProgress(progressPercentage)
                }
            }
            val body = request.body<String>()
            Success(body)
        }
        catch (e: Exception) {
            Failure(e)
        }
    }

    private suspend fun makeMultiPartRequest(
        httpMethod: HttpMethod,
        endPoint: String,
        requestBody: TestRequestBody,
        byteArray: ByteArray,
        onUpdateProgress: (Double) -> Unit
    ) {
        client.request(endPoint) {
            timeout {
                requestTimeoutMillis = 30000
            }
            method = httpMethod
            headers {
                append("type", "multi")
            }
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("sender", requestBody.sender)
                        append("message", requestBody.test)
                        append(
                            key = "image",
                            value = byteArray,
                            headers = Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=\"ktor_logo.png\"")
                            }
                        )
                    }
                )
            )
            onUpload { bytesSentTotal, contentLength ->
                val progressPercentage = 100.0 * bytesSentTotal/contentLength
                onUpdateProgress(progressPercentage)
            }
        }
    }

}
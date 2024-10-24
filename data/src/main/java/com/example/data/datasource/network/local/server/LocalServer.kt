package com.example.data.datasource.network.local.server

import com.example.data.datasource.network.local.model.test.ReceivedData
import com.example.data.datasource.network.local.model.test.TestRequestBody
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.request.receiveMultipart
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext

class LocalServer(private val port: Int) {

    val server: SharedFlow<ReceivedData> by lazy {
        callbackFlow {
            embeddedServer(Netty, port = this@LocalServer.port) {
                install(ContentNegotiation) {
                    json()
                }
                routing {
                    get("/api/data") {
//                        trySend(call.receiveText())
                        call.respondText("Hello, world!")
                    }
                    post("/initialize-table") {
//                        trySend(call.receiveText())
                        call.respondText("Success")
                    }
                    post("/test") {
                        val type = call.request.headers["type"]
                        if (type == "multi") {
                            var message = ""
                            var fileName = ""
                            var fileBytes = byteArrayOf()
                            withContext(Dispatchers.IO) {
                                call.receiveMultipart().forEachPart { partData: PartData ->
                                    when(partData) {
                                        is PartData.FormItem -> {
                                            message = partData.value
                                        }
                                        is PartData.FileItem -> {
                                            fileName = partData.originalFileName as String
                                            fileBytes = partData.streamProvider.invoke().readBytes()
                                        }
                                        else -> {}
                                    }
                                }
                            }
                            send(ReceivedData(testRequestBody = TestRequestBody(test = message, sender = "xxx"), fileName = fileName, fileBytes = fileBytes))
                            call.respondText("Success")
                        }
                        else {
                            val request = call.receive<TestRequestBody>()
                            send(ReceivedData(testRequestBody = request, fileName = null, fileBytes = null))
                            call.respondText("Success")
                        }
                    }
                }
            }.start(wait = true)
            awaitClose {
                cancel()
            }
        }.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(),
            replay = 0
        )
    }

}
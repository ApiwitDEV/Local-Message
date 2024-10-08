package com.example.data.datasource.network.local.server

import com.example.data.datasource.network.local.model.test.TestRequestBody
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.shareIn

class LocalServer(private val port: Int) {

    val server: SharedFlow<TestRequestBody> by lazy {
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
                        val request = call.receive<TestRequestBody>()
                        send(request)
                        call.respondText("Success")
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
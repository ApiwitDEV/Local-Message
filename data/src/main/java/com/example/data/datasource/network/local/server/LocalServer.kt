package com.example.data.datasource.network.local.server

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.Netty
import io.ktor.server.request.receiveText
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LocalServer(private val port: Int) {

    fun initializeServer(): Flow<String> {
        return callbackFlow {
            embeddedServer(Netty, port = this@LocalServer.port) {
                routing {
                    get("/api/data") {
                        trySend(call.receiveText())
                        call.respondText("Hello, world!")
                    }
                    post("/initialize-table") {
                        trySend(call.receiveText())
                        call.respondText("Success")
                    }
                    post("/test") {
                        trySend(call.receiveText())
                        call.respondText("Success")
                    }
                }
            }.start(wait = true)
            awaitClose {
                cancel()
            }
        }
    }

}
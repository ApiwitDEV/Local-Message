package com.example.data.repository

import com.example.data.datasource.network.local.server.LocalServer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ServerRepository(
    private val localServer: LocalServer
) {

    fun initializeServer() {
        CoroutineScope(Dispatchers.IO).launch {
            localServer.initializeServer()
                .collectLatest {
                    //TODO
                }
        }
    }

}
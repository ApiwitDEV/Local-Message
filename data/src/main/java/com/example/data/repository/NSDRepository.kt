package com.example.data.repository

import com.example.data.datasource.network.local.DNSBaseService
import com.example.data.datasource.network.local.client.NSD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NSDRepository(
    private val nsd: NSD,
    private val dnsBaseService: DNSBaseService
) {
    data class LocalNetworkService(
        val domainName: String,
        val ipAddress: String
    )

    fun initializeNSD() {
        CoroutineScope(Dispatchers.IO).launch {
            nsd.initialize()
                .collectLatest {

                }
        }
    }

    fun findLocalNetworkService() = dnsBaseService.serviceListFlow
        .map {
            it.map { item ->
                LocalNetworkService(
                    domainName = item.serviceInfo?.serviceName ?: "",
                    ipAddress = item.serviceInfo?.host?.hostAddress ?: ""
                )
            }
        }
}
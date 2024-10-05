package com.example.data.datasource.network.local

import com.example.data.datasource.network.local.model.DiscoveryServiceResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class DNSBaseService {

    private val serviceList = mutableListOf<DiscoveryServiceResult>()

    private val _serviceListFlow = MutableSharedFlow<List<DiscoveryServiceResult>>()
    val serviceListFlow = _serviceListFlow.asSharedFlow()

    fun collectDiscoveryService(service: DiscoveryServiceResult) {
        serviceList.add(service)
        _serviceListFlow.tryEmit(serviceList)
    }

    fun removeDiscoveryService(service: DiscoveryServiceResult) {
        serviceList.removeAll { service.serviceInfo?.serviceName == it.serviceInfo?.serviceName }
        _serviceListFlow.tryEmit(serviceList)
    }

    fun getIpAddressList(): List<String> {
        return serviceList
            .filter { it.serviceInfo?.serviceName?.contains("bas") == true }
            .map {
            val ipAddress = it.serviceInfo?.host?.hostAddress
            val port = it.serviceInfo?.port
            "http://$ipAddress:$port"
        }
    }

}
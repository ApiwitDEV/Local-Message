package com.example.data.datasource.network.local.model

import android.net.nsd.NsdServiceInfo

data class DiscoveryServiceResult(
    val serviceInfo: NsdServiceInfo?,
    val serviceType: String?,
    val errorCode: Int?,
    val status: String?
)
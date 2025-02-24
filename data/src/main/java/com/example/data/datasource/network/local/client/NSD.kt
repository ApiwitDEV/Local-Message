package com.example.data.datasource.network.local.client

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.os.Build
import android.util.Log
import com.example.data.datasource.network.local.DNSBaseService
import com.example.data.datasource.network.local.model.DiscoveryServiceResult
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

class NSD @Inject constructor(
    private val dnsBaseService: DNSBaseService,
    private val port: Int,
    private val context: Context
) {

    private var nsdManager: NsdManager? = null
    private val httpTcpServiceType = "_http._tcp."
//    private val printerTcpServiceType = "_ipp._tcp."

    val myServiceName = "MODEL: ${Build.MODEL} ID: ${Build.ID}"

    private val tag = "NSD log"

    fun initialize() = callbackFlow {
        fun discoveryService() {
            nsdManager?.discoverServices(
                httpTcpServiceType,
                NsdManager.PROTOCOL_DNS_SD,
                object : NsdManager.DiscoveryListener {
                    override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                        Log.d(tag, "onStartDiscoveryFailed: $errorCode")
                    }

                    override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                        Log.d(tag, "onStopDiscoveryFailed: $errorCode")
                    }

                    override fun onDiscoveryStarted(serviceType: String?) {
                        Log.d(tag, "onDiscoveryStarted: $serviceType")
                    }

                    override fun onDiscoveryStopped(serviceType: String?) {
                        Log.d("NSD log", "onDiscoveryStopped: $serviceType")
                    }

                    override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                        Log.d(tag, "onServiceFound: $serviceInfo")
                        serviceInfo?.also {
                            nsdManager?.resolveService(
                                serviceInfo,
                                object : NsdManager.ResolveListener {
                                    override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                                        Log.d(tag, "onResolveFailed: $serviceInfo errorCode: $errorCode")
                                        val service = DiscoveryServiceResult(
                                            serviceInfo = serviceInfo,
                                            serviceType = serviceInfo?.serviceType,
                                            errorCode = null,
                                            status = "fail"
                                        )
                                        trySend(service)
                                    }

                                    override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                                        Log.d(tag, "onServiceResolved: $serviceInfo")
                                        val service = DiscoveryServiceResult(
                                            serviceInfo = serviceInfo,
                                            serviceType = serviceInfo?.serviceType,
                                            errorCode = null,
                                            status = "success"
                                        )
                                        dnsBaseService.collectDiscoveryService(service)
                                        trySend(service)
                                    }
                                }
                            )
                        }
                    }

                    override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                        Log.d(tag, "onServiceLost: $serviceInfo")
                        serviceInfo?.also {
                            if (it.serviceType == httpTcpServiceType) {
                                val service = DiscoveryServiceResult(
                                    serviceInfo = it,
                                    serviceType = it.serviceType,
                                    errorCode = null,
                                    status = "lost"
                                )
                                trySend(service)
                                dnsBaseService.removeDiscoveryService(service)
                            }
                        }
                    }
                }
            )
        }

        val serviceInfo = NsdServiceInfo().apply {
            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceName = myServiceName
            serviceType = httpTcpServiceType
            port = this@NSD.port
        }

        nsdManager = (context.getSystemService(Context.NSD_SERVICE) as NsdManager).apply {
            registerService(
                serviceInfo,
                NsdManager.PROTOCOL_DNS_SD,
                object: NsdManager.RegistrationListener {
                    override fun onRegistrationFailed(
                        serviceInfo: NsdServiceInfo?,
                        errorCode: Int
                    ) {
                        Log.d(tag, "onRegistrationFailed: $errorCode")
                    }

                    override fun onUnregistrationFailed(
                        serviceInfo: NsdServiceInfo?,
                        errorCode: Int
                    ) {}

                    override fun onServiceRegistered(serviceInfo: NsdServiceInfo?) {
                        Log.d("NSD log", "onServiceRegistered: $serviceInfo")
                        discoveryService()
                    }

                    override fun onServiceUnregistered(serviceInfo: NsdServiceInfo?) {}
                }
            )
        }
        awaitClose { cancel() }
    }

}
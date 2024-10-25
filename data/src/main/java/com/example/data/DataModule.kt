package com.example.data

import com.example.data.datasource.network.local.DNSBaseService
import com.example.data.datasource.network.local.client.LocalService
import com.example.data.datasource.network.local.client.NSD
import com.example.data.datasource.network.local.server.LocalServer
import com.example.data.datasource.ondevice.database.chathistory.ChatDatabase
import com.example.data.repository.ImageRepository
import com.example.data.repository.NSDRepository
import com.example.data.repository.ServerRepository
import com.example.data.repository.MessageRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    single { NSD(get(), port = NetworkSpec.PORT.value, androidApplication()) }
    single { LocalServer(port = NetworkSpec.PORT.value) }
    single { MessageRepository(get(), get(), ChatDatabase.getInstance(androidContext()).chatDao()) }
    single { ServerRepository(get(), ChatDatabase.getInstance(androidContext()).chatDao()) }
    singleOf(::DNSBaseService)
    singleOf(::LocalService)
    singleOf(::NSDRepository)
    singleOf(::ImageRepository)
}
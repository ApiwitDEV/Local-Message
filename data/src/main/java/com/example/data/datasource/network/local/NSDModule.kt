package com.example.data.datasource.network.local

import android.content.Context
import com.example.data.datasource.network.local.client.NSD
import com.example.data.datasource.network.local.server.LocalServer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NSDModule {

    @Singleton
    @Provides
    fun provideLocalServer() = LocalServer(8080)

    @Singleton
    @Provides
    fun provideNSDService(@ApplicationContext context: Context, dnsBaseService: DNSBaseService): NSD {
        return NSD(
            dnsBaseService = dnsBaseService,
            port = 8080,
            context = context
        )
    }

}
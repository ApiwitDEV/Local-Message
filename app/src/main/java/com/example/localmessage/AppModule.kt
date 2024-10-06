package com.example.localmessage

import com.example.localmessage.feature.message.stateholder.HomeViewModel
import com.example.localmessage.ui.AppViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::HomeViewModel)
}
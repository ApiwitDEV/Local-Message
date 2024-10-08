package com.example.localmessage.feature.message.uistatemodel

data class HistoryItemUIState(
    val domainName: String,
    val ipAddress: String,
    val message: String,
    val sender: String,
    val type: String
)
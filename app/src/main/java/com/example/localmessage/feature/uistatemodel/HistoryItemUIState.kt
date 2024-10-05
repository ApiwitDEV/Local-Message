package com.example.localmessage.feature.uistatemodel

data class HistoryItemUIState(
    val domainName: String,
    val ipAddress: String,
    val requestMessage: String,
    val responseMessage: String,
    val sender: String,
    val type: String
)
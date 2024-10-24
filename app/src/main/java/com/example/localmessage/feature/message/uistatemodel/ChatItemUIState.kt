package com.example.localmessage.feature.message.uistatemodel

import android.graphics.Bitmap

data class ChatItemUIState(
    val id: Int?,
    val message: String?,
    val sender: String?,
    val type: String?,
    val image: Bitmap?,
    val progress: Double?
)
package com.example.localmessage.feature.message.uistatemodel

import android.graphics.Bitmap

data class ServiceActionUIState(
    val message: String?,
    val image: Bitmap?,
    val isLoadingImage: Boolean
)

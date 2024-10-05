package com.example.data.datasource.network.local.model.test

import kotlinx.serialization.Serializable

@Serializable
data class TestRequestBody(
    val test: String,
    val sender: String
)
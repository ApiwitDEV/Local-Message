package com.example.data

open class ResultData<T>

data class Success<T>(val data: T): ResultData<T>()

data class Failure<T>(val error: Exception): ResultData<T>()

fun <T> ResultData<T>.onSuccess(action: (T) -> Unit): ResultData<T> {
    if (this is Success) {
        action(this.data)
    }
    return this
}

fun <T> ResultData<T>.onFailure(action: (Exception) -> Unit): ResultData<T> {
    if (this is Failure) {
        action(this.error)
    }
    return this
}
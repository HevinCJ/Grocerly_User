package com.example.grocerly.utils

sealed class NetworkResult<T>(val data:T?=null,val message:String?=null) {
    class Success<T>(data: T):NetworkResult<T>(data)
    class Loading<T>:NetworkResult<T>()
    class UnSpecified<T>:NetworkResult<T>()
    class Error<T>(message: String?):NetworkResult<T>(data = null,message)
}
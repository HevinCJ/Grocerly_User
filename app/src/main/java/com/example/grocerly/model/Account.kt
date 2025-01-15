package com.example.grocerly.model

data class Account(
    val firstName:String,
    val lastName:String,
    val email:String,
    val imageUrl:String = " "
){
    constructor():this("","","","")
}

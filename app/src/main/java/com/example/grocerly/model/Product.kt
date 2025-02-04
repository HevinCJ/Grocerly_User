package com.example.grocerly.model

import com.example.grocerly.utils.ProductCategory

data class Product(
    val image:String?,
    val itemName:String,
    val itemPrice:Int?,
    val category: ProductCategory,
    val itemRating:Double ?= null,
    val totalRating:Int ?= null
){
    constructor():this("","",null, ProductCategory.selectcatgory,null,null)
}

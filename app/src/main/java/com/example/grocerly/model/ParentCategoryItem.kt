package com.example.grocerly.model

data class ParentCategoryItem(
    val categoryName: String,
    val childCategoryItems: List<ChildCategoryItem>
)

package com.example.getdriver.data.local.model

import com.google.gson.annotations.SerializedName

data class OrderList(
    @SerializedName("page")
    val pageNumber: Int = 0,
    val content: List<Orders>
)
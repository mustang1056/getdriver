package com.example.getdriver.data.local.model

import com.google.gson.annotations.SerializedName

data class YandexAddress(
    @SerializedName("street_name")
    val street_name: String
)
package com.example.getdriver.data.remote

import com.example.getdriver.data.local.model.OrderList
import com.example.getdriver.data.local.model.YandexAddress
import com.skydoves.sandwich.ApiResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YandexMapService {
    @GET("getaddress/{longlat}")
    suspend fun getAddress(@Path("longlat") longlat: (String)): ApiResponse<YandexAddress>
}
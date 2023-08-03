package com.auto.getremont.data.remote

import com.example.getdriver.data.local.model.OrderList
import com.example.getdriver.data.local.model.Orders
import com.skydoves.sandwich.ApiResponse
import okhttp3.MultipartBody


import retrofit2.Response
import retrofit2.http.*

interface OrderService {
    @GET("orders")
    suspend fun getAllRemonts(@Query("page") page: (Int)): ApiResponse<OrderList>

    @GET("orders")
    suspend fun getAllRemontsTest(@Query("page") page: (Int)): Response<OrderList>

    @GET("orders")
    suspend fun getAllRemont(@Query("page") page:Int) : Response<Orders>

    @GET("order/{id}")
    suspend fun getRemont(@Path("id") id: Int): Response<Orders>

    @POST("order")
    suspend fun addRemont(@Body blog: Orders):ApiResponse<OrderList>

    @Multipart
    @POST("/remont/upload")
    suspend fun postImage(@Part image: MultipartBody.Part): ApiResponse<OrderList>
}
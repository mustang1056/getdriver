package com.auto.getremont.data.remote

import androidx.room.FtsOptions
import com.example.getdriver.data.local.model.Orders
import javax.inject.Inject

class OrderRemoteDataSource @Inject constructor(
    private val remontService: OrderService
): BaseDataSource() {

    suspend fun getOrders(page: Int) = remontService.getAllRemonts(page)
    suspend fun getOrder(id: Int) = getResult { remontService.getRemont(id) }
    suspend fun addOrder(remont: Orders) = remontService.addRemont(remont)

}
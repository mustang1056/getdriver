package com.example.getdriver.viewmodels

import androidx.lifecycle.ViewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.auto.getremont.data.remote.OrderService
import com.example.getdriver.data.local.AppDatabase
import com.example.getdriver.data.local.dao.OrderDao
import com.example.getdriver.data.local.model.Orders
import com.example.getdriver.data.paging.OrderRemoteMediator

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderService: OrderService,
    private val orderDao: OrderDao,
    private val db: AppDatabase
) : ViewModel() {
    lateinit var clickedItem: Orders
    /*
    val movies: Flow<PagingData<Remont>> = Pager(PagingConfig(pageSize = 10),) {
        RemontSource(mainRepository)
    }.flow
     */

    @ExperimentalPagingApi
    val orders: Flow<PagingData<Orders>> =  Pager(
        config = PagingConfig(pageSize = 20),
        remoteMediator = OrderRemoteMediator(
            orderService,
            db
        ),
        pagingSourceFactory = { orderDao.pagingSource()}).flow

    fun itemClicked(item: Orders) {
        clickedItem = item
    }
}
package com.example.getdriver.data.repository

import androidx.annotation.WorkerThread
import com.auto.getremont.data.remote.OrderService
import com.example.getdriver.data.local.dao.OrderDao
import com.example.getdriver.data.local.model.Orders
import com.example.getdriver.data.remote.YandexMapService
import com.skydoves.sandwich.message
import com.skydoves.sandwich.onError
import com.skydoves.sandwich.onException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import timber.log.Timber
import javax.inject.Inject

class OrdersRepository @Inject constructor(
    private val orderService: OrderService,
    private val orderDao: OrderDao
    ) {
    init {
        Timber.d("Injection MainRepository")
    }

    @WorkerThread
    fun load(
        onStart: () -> Unit,
        onCompletion: () -> Unit,
        onError: (String) -> Unit,
        page: (Int) -> Unit
    ) = flow {
        // request API network call asynchronously.
        orderService.getAllOrders(0)
            // handle the case when the API request gets a success response.
            .suspendOnSuccess {
                orderDao.deleteAll()
                orderDao.insertAll(data.content)
                val posters: List<Orders> = orderDao.getAllRemonts()
                emit(posters)
            }
            // handle the case when the API request gets an error response.
            // e.g. internal server error.
            .onError {
                onError(message())
            }
            // handle the case when the API request gets an exception response.
            // e.g. network connection error.
            .onException {
                onError(message())
            }

    }.onStart { onStart() }.onCompletion { onCompletion() }.flowOn(Dispatchers.IO)


    @WorkerThread
    fun addOrder(onStart: () -> Unit,
                onCompletion: () -> Unit,
                onError: (String) -> Unit,
                remont: Orders)= flow {
        // request API network call asynchronously.
        orderService.addOrder(remont)
            // handle the case when the API request gets a success response.
            .suspendOnSuccess {
                //posterDao.insertAll(data.content)
                emit(data.content)
                onCompletion()
            }
            // handle the case when the API request gets an error response.
            // e.g. internal server error.
            .onError {
                onError(message())

                //Toast.makeText(this, "qwdqwd", Toast.LENGTH_SHORT).show()

            }
            // handle the case when the API request gets an exception response.
            // e.g. network connection error.
            .onException {
                onError(message())

            }
    }.onStart { onStart() }.onCompletion { }.flowOn(Dispatchers.IO)



}
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
import javax.inject.Inject
import com.skydoves.sandwich.message
import timber.log.Timber


class YandexMapRespository @Inject constructor(
    private val yandexMapService: YandexMapService,
) {
    init {
        Timber.d("Injection MainRepository")
    }
    //suspend fun getAllMovies(longlat : String) = yandexMapService.getAddress(longlat)

    @WorkerThread
    fun getAddress(onStart: () -> Unit,
                 onCompletion: () -> Unit,
                 onError: (String) -> Unit,
                 longlat: String
    )= flow {
        // request API network call asynchronously.
        yandexMapService.getAddress(longlat)
            // handle the case when the API request gets a success response.
            .suspendOnSuccess {
                //posterDao.insertAll(data.content)
                emit(data.street_name)
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
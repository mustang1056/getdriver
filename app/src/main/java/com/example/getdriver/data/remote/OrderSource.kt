package com.auto.ingram.data.remote

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.getdriver.data.local.model.Orders
import com.example.getdriver.data.repository.OrdersRepository
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import timber.log.Timber
import javax.inject.Inject

class OrderSource @Inject constructor(
    private val movieRepository: OrdersRepository
) : PagingSource<Int, Orders>() {

    private val _isLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoading: State<Boolean> get() = _isLoading
    @InternalCoroutinesApi
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Orders> {
        return try {
            val nextPage = params.key ?: 1

                val posterList: Flow<Orders> =
                    movieRepository.load(
                        onStart = { _isLoading.value = true },
                        onCompletion = { _isLoading.value = false },
                        onError = { Timber.d(it) },
                        page = { 0 }
                    ) as Flow<Orders>

                posterList.collect {

                }

                LoadResult.Page(
                    data = posterList.toList(),
                    prevKey = if (nextPage == 1) null else nextPage - 1,
                    nextKey = if (posterList.toList().isEmpty()) null else nextPage + 1
                )


            } catch (e: Exception) {
                LoadResult.Error(e)
            }
    }



    @ExperimentalPagingApi
    override fun getRefreshKey(state: PagingState<Int, Orders>): Int? {
        TODO("Not yet implemented")
    }
}
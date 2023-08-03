package com.example.getdriver.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.auto.getremont.data.remote.OrderService

import com.example.getdriver.data.local.AppDatabase
import com.example.getdriver.data.local.model.Orders
import com.example.getdriver.data.local.model.RemoteKey


@ExperimentalPagingApi
class OrderRemoteMediator(
    private val unsplashApi: OrderService,
    private val unsplashDatabase: AppDatabase
) : RemoteMediator<Int, Orders>() {

    private val unsplashImageDao = unsplashDatabase.orderDao()
    private val unsplashRemoteKeysDao = unsplashDatabase.pageKeyDao()

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Orders>
    ): MediatorResult {
        return try {
            val currentPage = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 0
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null
                        )
                    nextPage
                }
            }

            val response = unsplashApi.getAllRemontsTest(currentPage).body()
            println(response?.content)

            val endOfPaginationReached = response?.content?.isEmpty()

            val prevPage = if (currentPage == 1) null else currentPage - 1
            val nextPage = if (endOfPaginationReached!!) null else currentPage + 1


            unsplashDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    unsplashImageDao.deleteAll()
                    unsplashRemoteKeysDao.deleteAllMovieRemoteKeys()
                }
                val keys = response.content.map { unsplashImage ->
                    RemoteKey(
                        id = unsplashImage.id,
                        prevPage = prevPage,
                        nextPage = nextPage,
                        lastUpdated = null
                    )
                }

                unsplashRemoteKeysDao.addAllMovieRemoteKeys(keys)
                unsplashImageDao.insertAll(response.content)
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(
        state: PagingState<Int, Orders>
    ): RemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                unsplashRemoteKeysDao.getMovieRemoteKeys(movieId = id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, Orders>
    ): RemoteKey? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeysDao.getMovieRemoteKeys(movieId = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, Orders>
    ): RemoteKey? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeysDao.getMovieRemoteKeys(movieId = unsplashImage.id)
            }
    }

}
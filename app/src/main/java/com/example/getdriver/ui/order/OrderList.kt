package com.example.getdriver.ui.order

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.example.getdriver.data.local.model.Orders
import com.example.getdriver.viewmodels.OrdersViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


import kotlinx.coroutines.delay

private val SaveMap = mutableMapOf<String, KeyParams>()
private var isRefreshing = false

private data class KeyParams(
    val params: String = "",
    val index: Int,
    val scrollOffset: Int
)


@ExperimentalPagingApi
@OptIn(ExperimentalMaterialApi::class)
@ExperimentalFoundationApi
@Composable
fun MainList(navController: NavController, viewModel : OrdersViewModel
){

    //val posters: List<Remont> by mainViewModel.posterList.collectAsState(initial = listOf())
    val lazyMovieItems: LazyPagingItems<Orders> = viewModel.movies.collectAsLazyPagingItems()


    RemontList(navController = navController,movieList = lazyMovieItems,
        onItemClicked = viewModel::itemClicked)
}

@Composable
fun ComposableLifecycle(
    lifeCycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    onEvent: (LifecycleOwner, Lifecycle.Event) -> Unit
) {
    DisposableEffect(lifeCycleOwner) {
        val observer = LifecycleEventObserver { source, event ->
            onEvent(source, event)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}


fun sendMsg(refresh: Boolean){
    isRefreshing = refresh
}


@Composable
fun rememberForeverLazyListState(
    key: String,
    params: String = "",
    initialFirstVisibleItemIndex: Int = 0,
    initialFirstVisibleItemScrollOffset: Int = 0
): LazyListState {
    val scrollState = rememberSaveable(saver = LazyListState.Saver) {
        var savedValue = SaveMap[key]
        if (savedValue?.params != params) savedValue = null
        val savedIndex = savedValue?.index ?: initialFirstVisibleItemIndex
        val savedOffset = savedValue?.scrollOffset ?: initialFirstVisibleItemScrollOffset
        LazyListState(
            savedIndex,
            savedOffset
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            val lastIndex = scrollState.firstVisibleItemIndex
            val lastOffset = scrollState.firstVisibleItemScrollOffset
            SaveMap[key] = KeyParams(params, lastIndex, lastOffset)
        }
    }
    return scrollState
}


@ExperimentalPagingApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun RemontList(
    navController: NavController,
    movieList:LazyPagingItems<Orders>,
    onItemClicked:(item:Orders) ->Unit
) {
    var listState = rememberLazyListState()
    val Red = Color(red = 35, green = 61, blue = 83)

    /*
    val viewModel = hiltViewModel<RemontViewModel>()

    val lazyMovieItems: LazyPagingItems<Remont> = viewModel.movies.collectAsLazyPagingItems()
*/

    /*
    LazyColumn(state = listState) {
        itemsIndexed(movieList){index, item ->
            ListViewItem(navController = navController,remontItem = item,onItemClicked)
        }
    }*/

    var refreshing by remember { mutableStateOf(false) }
    LaunchedEffect(refreshing) {
        if (refreshing) {
            delay(3000)
            movieList.refresh()
            refreshing = false
        }
    }

    ComposableLifecycle { source, event ->
        if (event == Lifecycle.Event.ON_RESUME) {

            if(isRefreshing == true) {
                movieList.refresh()
                isRefreshing = false
            }
        }
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing = refreshing),
        onRefresh = { refreshing = true },
    ) {

        LazyColumn(contentPadding = PaddingValues(horizontal = 10.dp, vertical = 16.dp),
            // New vertical spacing
            verticalArrangement = Arrangement.spacedBy(12.dp),
            state = rememberForeverLazyListState(key = "Overview")
        ) {


            items(items = movieList, itemContent = { item ->

                if (item != null) {
                    ListViewItem(navController = navController, remontItem = item, onItemClicked)
                }

            })

            movieList.apply {
                when {
                    loadState.refresh is LoadState.Loading -> {
                        item { /*LoadingView(modifier = Modifier.fillParentMaxSize())*/ }
                    }
                    loadState.append is LoadState.Loading -> {
                        item { /*LoadingItem()*/ }
                    }
                    loadState.refresh is LoadState.Error -> {
                        val e = movieList.loadState.refresh as LoadState.Error
                        item {
                            /*ErrorItem(
                            message = e.error.localizedMessage!!,
                            modifier = Modifier.fillParentMaxSize(),
                            onClickRetry = { retry() }
                        )*/
                        }
                    }
                    loadState.append is LoadState.Error -> {
                        val e = movieList.loadState.append as LoadState.Error
                        item {
                            /*ErrorItem(
                            message = e.error.localizedMessage!!,
                            onClickRetry = { retry() }
                        )*/
                        }
                    }
                }
            }
        }
    }



    @Composable
    fun LoadingView(modifier: Any) {

    }
}
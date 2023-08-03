package com.auto.ingram.ui.navigation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.paging.ExperimentalPagingApi
import com.example.getdriver.viewmodels.OrdersViewModel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.example.getdriver.R


@Composable
fun MainWindows(){
    val viewModel = hiltViewModel<OrdersViewModel>()

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = BottomNavItem.RemontList.screen_route
    ) {
        composable(BottomNavItem.RemontList.screen_route
        ) {
            MainScreenView(viewModel, navController)
        }
        composable(BottomNavItem.RemontAddForm.screen_route
        ) {
            MainScreenView(viewModel, navController)
        }
        composable(BottomNavItem.RemontDetail.screen_route) {
                backStackEntry -> val posterId =
            backStackEntry.arguments?.getLong(NavScreen.PosterDetails.argument0) ?: return@composable
            //OrderDetails(viewModel.clickedItem, navController)
        }
        composable(BottomNavItem.MyNetwork.screen_route) {
            NetworkScreen(navController)
        }
    }
}


@Composable
fun MainScreenView(viewModel: OrdersViewModel, navController: NavController){
    val navController1 = rememberNavController()
    val scaffoldState = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))
    val scope = rememberCoroutineScope()
    Scaffold(
        drawerBackgroundColor = colorResource(id = R.color.purple_200),
        drawerContent = {
            Drawer(scope = scope, scaffoldState = scaffoldState, navController = navController)
        },
        scaffoldState = scaffoldState,
        topBar = { TopBar(scope = scope, scaffoldState = scaffoldState) },
        bottomBar = { BottomNavigation(navController = navController1) },
        // Defaults to false
        isFloatingActionButtonDocked = false,
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen
    ) {
            innerPadding ->
        Box(modifier = androidx.compose.ui.Modifier.padding(innerPadding)) {
            NavHost(navController1, startDestination = BottomNavItem.Home.screen_route) {
                composable(BottomNavItem.Home.screen_route) {
                    BlogScreen(navController, viewModel)
                }
                composable(BottomNavItem.MyNetwork.screen_route) {
                    NetworkScreen(navController)
                }
                composable(BottomNavItem.AddPost.screen_route) {
                    AddPostScreen(navController)
                }
            }
        }
    }
}

@Composable
fun TopBar(scope: CoroutineScope, scaffoldState: ScaffoldState) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.app_name), fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    scaffoldState.drawerState.open()
                }
            }) {
                Icon(Icons.Filled.Menu, "")
            }
        },
        backgroundColor = colorResource(id = R.color.purple_200),
        contentColor = Color.White
    )
}

@Composable
fun BottomNavigation(navController: NavController) {
    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.MyNetwork,
        BottomNavItem.AddPost
    )
    BottomNavigation(
        backgroundColor = colorResource(id = R.color.orange),
        contentColor = Color.Black
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            BottomNavigationItem(
                icon = { Icon(painterResource(id = item.icon), contentDescription = item.screen_route) },
                label = { Text(text = item.screen_route,
                    fontSize = 9.sp) },
                selectedContentColor = Color.Black,
                unselectedContentColor = Color.Black.copy(0.4f),
                alwaysShowLabel = true,
                selected = currentRoute == item.screen_route,
                onClick = {
                    navController.navigate(item.screen_route) {

                        navController.graph.startDestinationRoute?.let { screen_route ->
                            popUpTo(screen_route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}




sealed class NavScreen(val route: String) {

    object Home : NavScreen("trendingMovieList")

    object PosterDetails : NavScreen("trendingMovieList") {

        const val routeWithArgument: String = "trendingMovieList/{posterId}"

        const val argument0: String = "posterId"
    }

}


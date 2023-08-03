package com.auto.ingram.ui.navigation

import com.example.getdriver.R


sealed class BottomNavItem(var title:String, var icon:Int, var screen_route:String){

    object Home : BottomNavItem("Home", R.drawable.ic_home,"home")
    object MyNetwork: BottomNavItem("My Network",R.drawable.ic_my_network,"my_network")
    object AddPost: BottomNavItem("Post",R.drawable.ic_post,"add_post")

    object RemontDetail : BottomNavItem("remontDetail", R.drawable.ic_launcher_background, "remontDetail")

    object RemontList : BottomNavItem("remontList", R.drawable.ic_launcher_background, "RemontList")

    object RemontAddForm : BottomNavItem("remontForm", R.drawable.ic_launcher_background, "RemontForm")
}
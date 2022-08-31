package com.example.composetodolist

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.composetodolist.ui.theme.home.HomeScreen
import com.example.composetodolist.ui.theme.home.detail.DetailScreen

sealed class NavRoute(val route: String) {
    object Home : NavRoute("home_route")
    object Detail : NavRoute("detail_route")
}

@Composable
fun TodoNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = NavRoute.Home.route) {
        composable(NavRoute.Home.route) {
            HomeScreen {
                navController.navigate(NavRoute.Detail.route + "/${it?.id ?: -1}") {

                }
            }
        }
        composable(
            NavRoute.Detail.route + "/{id}",
            arguments = listOf(navArgument("id"){ type = NavType.LongType })
        ) {
            DetailScreen(selectedId = it.arguments?.getLong("id") ?: -1) {
                navController.navigateUp()
            }
        }
    }
}
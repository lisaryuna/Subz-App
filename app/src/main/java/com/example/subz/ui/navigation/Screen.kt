package com.example.subz.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("Login")
    object Register : Screen("Register")
    object Home : Screen("home")
    object Search : Screen("search")
    object Profile : Screen("profile")
    object AddSubscription : Screen("add_subscription")
    object DetailSubscription : Screen("detail_subscription/{id}") {
        fun createRoute(id: Int) = "detail_subscription/$id"
    }
    object EditSubscription : Screen("edit_subscription/{id}") {
        fun createRoute(id: Int) = "edit_subscription/$id"
    }
}
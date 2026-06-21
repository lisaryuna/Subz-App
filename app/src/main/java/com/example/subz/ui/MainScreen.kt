package com.example.subz.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.subz.ui.components.BottomNavigationBar
import com.example.subz.ui.navigation.Screen
import com.example.subz.ui.screens.detail.DetailScreen
import com.example.subz.ui.screens.auth.LoginScreen
import com.example.subz.ui.screens.auth.RegisterScreen
import com.example.subz.ui.screens.manage.AddEditSubscriptionScreen
import com.example.subz.ui.screens.home.HomeScreen
import com.example.subz.ui.screens.profile.ProfileScreen
import com.example.subz.ui.screens.search.SearchScreen
import com.example.subz.ui.screens.splash.SplashScreen
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.viewmodel.AuthViewModel

@Composable
fun MainScreen(authViewModel: AuthViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val startDestination = Screen.Splash.route
    val noBottomBarRoutes = listOf(Screen.Splash.route, Screen.Login.route, Screen.Register.route, Screen.AddSubscription.route)

    Scaffold(
        bottomBar = {
            if (currentRoute !in noBottomBarRoutes) {
                BottomNavigationBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Home.route) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.AddSubscription.route)},
                    containerColor = PrimaryBlue,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Subscription")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen (
                    onNavigateNext = {
                        val nextRoute = if (authViewModel.currentUser != null) Screen.Home.route else Screen.Login.route
                        navController.navigate(nextRoute) {
                            popUpTo(Screen.Splash.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route)},
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route)},
                    onNavigateToHome = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onNavigateToDetail = { subId ->
                        navController.navigate(Screen.DetailSubscription.createRoute(subId))
                    }
                )
            }
            composable(Screen.Search.route) { SearchScreen(
                    onNavigateToDetail = { subId ->
                        navController.navigate(Screen.DetailSubscription.createRoute(subId))
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToLogin = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) {inclusive = true}
                        }
                    }
                )
            }
            composable(Screen.AddSubscription.route) {
                AddEditSubscriptionScreen(
                    subscriptionId = null,
                    onNavigateBack = { navController.popBackStack()}
                )
            }
            composable(Screen.EditSubscription.route) { backStackEntry ->
                val idString = backStackEntry.arguments?.getString("id")
                val id = idString?.toIntOrNull()

                AddEditSubscriptionScreen(
                    subscriptionId = id,
                    onNavigateBack = { navController.popBackStack()}
                )
            }
            composable(Screen.DetailSubscription.route) { backStackEntry ->
                val idString = backStackEntry.arguments?.getString("id")
                val id = idString?.toIntOrNull() ?: 0

                DetailScreen(
                    subscriptionId = id,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEdit = { subId ->
                        navController.navigate(Screen.EditSubscription.createRoute(subId))
                    }
                )
            }
        }
    }
}
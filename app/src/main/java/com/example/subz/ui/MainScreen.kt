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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.subz.ui.components.BottomNavigationBar
import com.example.subz.ui.navigation.Screen
import com.example.subz.ui.screens.add.AddSubscriptionScreen
import com.example.subz.ui.screens.home.HomeScreen
import com.example.subz.ui.screens.profile.ProfileScreen
import com.example.subz.ui.screens.search.SearchScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute != "add") {
                BottomNavigationBar(navController = navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Home.route) {
                FloatingActionButton(
                    onClick = { navController.navigate("add")}
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Subscription")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Search.route) { SearchScreen() }
            composable(Screen.Profile.route) { ProfileScreen() }
            composable("add") {
                AddSubscriptionScreen(
                    onNavigateBack = { navController.popBackStack()}
                )
            }
        }
    }
}
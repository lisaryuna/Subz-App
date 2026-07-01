package com.example.subz.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.subz.R
import com.example.subz.ui.navigation.Screen
import com.example.subz.ui.theme.IndicatorLightBlue
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.TextDarkNavy

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem(stringResource(id = R.string.nav_home), Screen.Home.route, Icons.Default.Home),
        BottomNavItem(stringResource(id = R.string.nav_search), Screen.Search.route, Icons.Default.Search),
        BottomNavItem(stringResource(id = R.string.nav_profile), Screen.Profile.route, Icons.Default.Person)
    )

    NavigationBar(
        containerColor = Color.White,
        contentColor = TextDarkNavy
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    if (selected) Text(text = item.title)
                },
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    indicatorColor = IndicatorLightBlue,
                    unselectedIconColor = TextDarkNavy,
                    unselectedTextColor = TextDarkNavy
                )
            )
        }
    }
}
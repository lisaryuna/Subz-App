package com.example.subz.ui.screens.home

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.ui.components.HomeStatsCard
import com.example.subz.ui.components.HomeSubscriptionList
import com.example.subz.ui.components.SubzEmptyState
import com.example.subz.ui.components.SubzTopAppBar
import com.example.subz.ui.theme.BackgroundLight
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.TextDarkNavy
import com.example.subz.ui.viewmodel.HomeViewModel
import com.example.subz.ui.viewmodel.UiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val uiState by viewModel.subscriptionsState.collectAsState()
    val totalActivePrice by viewModel.totalActivePrice.collectAsState()

    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    Scaffold(
        topBar = {
            SubzTopAppBar(title = "Subz")
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryBlue)
                }
            }
            is UiState.Error -> {
                SubzEmptyState(message = "Error: ${state.message}")
            }
            is UiState.Success -> {
                val subscriptions = state.data
                val contentModifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)

                if (isLandscape) {
                    Row(modifier = contentModifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            HomeStatsCard(totalActivePrice = totalActivePrice, activeCount = subscriptions.size)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "My Subscriptions",
                                color = TextDarkNavy,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                            )
                            HomeSubscriptionList(subscriptions = subscriptions, listState = listState, onNavigateToDetail = onNavigateToDetail)
                        }
                    }
                } else {
                    Column(modifier = contentModifier) {
                        HomeStatsCard(totalActivePrice = totalActivePrice, activeCount = subscriptions.size)
                        Text(
                            text = "My Subscriptions",
                            color = TextDarkNavy,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        HomeSubscriptionList(subscriptions = subscriptions, listState = listState, onNavigateToDetail = onNavigateToDetail)
                    }
                }
            }
        }
    }
}
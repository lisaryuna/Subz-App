package com.example.subz.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.ui.components.SubscriptionItem
import com.example.subz.ui.components.SubzEmptyState
import com.example.subz.ui.components.SubzTopAppBar
import com.example.subz.ui.theme.BackgroundLight
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.SecondaryLightBlue
import com.example.subz.ui.theme.TextDarkNavy
import com.example.subz.ui.viewmodel.HomeViewModel
import com.example.subz.utils.CurrencyFormatter

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val subscriptions by viewModel.subscriptions.collectAsState()
    val totalActivePrice by viewModel.totalActivePrice.collectAsState()

    Scaffold(
        topBar = {
            SubzTopAppBar(title = "Subz")
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 24.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(PrimaryBlue, SecondaryLightBlue)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "Total Active Subscriptions",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = CurrencyFormatter.formatRupiah(totalActivePrice),
                            color = Color.White,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = Color.White.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Active: ${subscriptions.size}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.BarChart,
                                contentDescription = "Stats",
                                tint = Color.White.copy(alpha = 0.6f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
            Text(
                text = "My Subscriptions",
                color = TextDarkNavy,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (subscriptions.isEmpty()) {
                SubzEmptyState(message = "No subscriptions added yet.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(subscriptions) { sub ->
                        SubscriptionItem(
                            sub = sub,
                            onClick = { onNavigateToDetail(sub.id) }
                            )
                    }
                }
            }
        }
    }
}
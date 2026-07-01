package com.example.subz.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.R
import com.example.subz.ui.components.SubscriptionItem
import com.example.subz.ui.components.SubzEmptyState
import com.example.subz.ui.theme.BackgroundLight
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.TextDarkNavy
import com.example.subz.ui.viewmodel.HomeViewModel
import com.example.subz.ui.viewmodel.UiState

@Composable
fun SearchScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val uiState by viewModel.subscriptionsState.collectAsState()
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    val subscriptions = if (uiState is UiState.Success) (uiState as UiState.Success).data else emptyList()
    val filters = listOf("All") + subscriptions.map { it.walletName }.distinct().filter { it.isNotBlank() }.sorted()

    val filteredSubscriptions = subscriptions.filter { data ->
        val matchesSearch = data.subscription.name.contains(searchQuery, ignoreCase = true)
        val matchesFilter = if (selectedFilter == "All") true else data.walletName == selectedFilter
        matchesSearch && matchesFilter
    }

    val filterState = rememberLazyListState()
    val resultState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it},
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringResource(id = R.string.search_placeholder), color = Color.Gray)},
            leadingIcon = {
                Icon(Icons.Outlined.Search, contentDescription = "Search", tint = PrimaryBlue)
            },
            shape = RoundedCornerShape(50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PrimaryBlue,
                unfocusedBorderColor = PrimaryBlue
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            state = filterState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) PrimaryBlue else Color.White,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) PrimaryBlue else Color.LightGray,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else TextDarkNavy,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (filteredSubscriptions.isEmpty()) {
            SubzEmptyState(message = stringResource(id = R.string.no_subscriptions_found))
        } else {
            LazyColumn(
                state = resultState,
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredSubscriptions) { data ->
                    SubscriptionItem(
                        item = data,
                        onClick = { onNavigateToDetail(data.subscription.id) }
                    )
                }
            }
        }
    }
}
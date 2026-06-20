package com.example.subz.ui.screens.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import java.util.Date
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.ui.components.SubzTopAppBar
import com.example.subz.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    subscriptionId: Int? = null,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val subscriptionToEdit by remember(subscriptionId) {
        if (subscriptionId != null) {
            viewModel.getSubscriptionById(subscriptionId)
        } else {
            flowOf(null)
        }
    }.collectAsState(initial = null)

    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var renewalDate by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    LaunchedEffect(subscriptionToEdit) {
        subscriptionToEdit?.let { sub ->
            name = sub.name
            price = if (sub.price % 1.0 == 0.0) sub.price.toInt().toString() else sub.price.toString()
            renewalDate = sub.renewalDate
            paymentMethod = sub.paymentMethod
        }
    }

    val isEditMode = subscriptionId != null
    val pageTitle = if (isEditMode) "Edit Subscription" else "New Subscription"
    val buttonText = if (isEditMode) "Update Changes" else "Save Subscription"

    Scaffold(
        topBar = {
            SubzTopAppBar(
                title = pageTitle,
                canNavigateBack = true,
                navigateUp = onNavigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it},
                label = { Text("Service name")},
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Price (Rp)")},
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = renewalDate,
                onValueChange = { },
                label = { Text("Renewal / Trial End")},
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true}) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select Date"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true}
            )

            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { millis ->
                                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                renewalDate = formatter.format(Date(millis))
                            }
                            showDatePicker = false
                        }) { Text("OK")}
                    },
                    dismissButton = {
                        TextButton(onClick = {showDatePicker = false }) { Text("Cancel") }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            OutlinedTextField(
                value = paymentMethod,
                onValueChange = { paymentMethod = it },
                label = { Text("Payment Method")},
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (name.isNotBlank() && price.isNotBlank()) {
                        if (isEditMode && subscriptionToEdit != null) {
                            viewModel.updateSubscription(
                                subscriptionToEdit!!.copy(
                                    name = name,
                                    price = price.toDoubleOrNull() ?: 0.0,
                                    renewalDate = renewalDate,
                                    paymentMethod = paymentMethod
                                )
                            )
                        } else {
                            viewModel.addSubscription(
                                name = name,
                                price = price.toDoubleOrNull() ?: 0.0,
                                renewalDate = renewalDate,
                                paymentMethod = paymentMethod
                            )
                        }
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(buttonText)
            }
        }
    }
}
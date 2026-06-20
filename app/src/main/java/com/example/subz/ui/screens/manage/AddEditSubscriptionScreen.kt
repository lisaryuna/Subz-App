package com.example.subz.ui.screens.manage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import java.util.Date
import java.util.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.data.local.entity.SubscriptionEntity
import com.example.subz.data.local.entity.WalletEntity
import com.example.subz.ui.components.SubzButton
import com.example.subz.ui.components.SubzClickableField
import com.example.subz.ui.components.SubzTextField
import com.example.subz.ui.components.SubzTopAppBar
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.TextDarkNavy
import com.example.subz.ui.viewmodel.HomeViewModel
import com.example.subz.ui.viewmodel.WalletViewModel
import kotlinx.coroutines.flow.flowOf
import java.text.SimpleDateFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditSubscriptionScreen(
    subscriptionId: Int? = null,
    viewModel: HomeViewModel = hiltViewModel(),
    walletViewModel: WalletViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val subscriptionToEdit by remember(subscriptionId) {
        if (subscriptionId != null) {
            viewModel.getSubscriptionById(subscriptionId)
        } else {
            flowOf<SubscriptionEntity?>(null)
        }
    }.collectAsState(initial = null)

    val wallets by walletViewModel.wallets.collectAsState()
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var renewalDate by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showWalletSelector by remember { mutableStateOf(false) }
    var showAddWalletDialog by remember { mutableStateOf(false) }
    var newWalletName by remember { mutableStateOf("") }

    LaunchedEffect(subscriptionToEdit) {
        subscriptionToEdit?.let { sub ->
            name = sub.name
            price =
                if (sub.price % 1.0 == 0.0) sub.price.toInt().toString() else sub.price.toString()
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
            SubzTextField(
                value = name,
                onValueChange = { name = it },
                label = "Service name",
                placeholder = "e.g., Netflix"
            )

            SubzTextField(
                value = price,
                onValueChange = { price = it },
                label = "Price (Rp)",
                placeholder = "e.g., 50000",
                keyboardType = KeyboardType.Number,
            )

            SubzClickableField(
                value = renewalDate,
                label = "Renewal / Trial End",
                trailingIcon = Icons.Default.DateRange,
                onClick = { showDatePicker = true }
            )

            SubzClickableField(
                value = paymentMethod,
                label = "Payment Method",
                trailingIcon = Icons.Default.KeyboardArrowDown,
                onClick = { showWalletSelector = true }
            )
            Spacer(modifier = Modifier.weight(1f))

            SubzButton(
                text = buttonText,
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
                }
            )
        }

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
                    }) { Text("OK") }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showWalletSelector) {
            WalletSelectorSheet(
                wallets = wallets,
                onDismiss = { showWalletSelector = false },
                onWalletSelected = { selected ->
                    paymentMethod = selected
                    showWalletSelector = false
                },
                onDeleteWallet = { walletViewModel.deleteWallet(it) },
                onAddNewClick = {
                    showWalletSelector = false
                    showAddWalletDialog = true
                }
            )
        }

        if (showAddWalletDialog) {
            AddWalletSheet(
                onDismiss = { showAddWalletDialog = false },
                onSave = { newName ->
                    walletViewModel.addWallet(newName)
                    showAddWalletDialog = false
                    showWalletSelector = true
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSelectorSheet(
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onWalletSelected: (String) -> Unit,
    onDeleteWallet: (WalletEntity) -> Unit,
    onAddNewClick: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp)
        ) { Text(
            text = "Select Payment Method",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )
            if (wallets.isEmpty()) {
                Text("No wallets added yet.", color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyColumn {
                    items(wallets) { wallet ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onWalletSelected(wallet.name) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = wallet.name, fontSize = 16.sp, color = TextDarkNavy)
                            IconButton(
                                onClick = { onDeleteWallet(wallet) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                            }
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
                onClick = onAddNewClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Add New Wallet", color = PrimaryBlue)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWalletSheet(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit) {
    var newWalletName by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp, start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Add New Wallet",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            SubzTextField(
                value = newWalletName,
                onValueChange = { newWalletName = it },
                label = "Wallet Name",
                placeholder = "e.g., Jenius, Jago"
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", color = TextDarkNavy)
                }
                Spacer(modifier = Modifier.width(8.dp))
                SubzButton(
                    text = "Save",
                    onClick = { if (newWalletName.isNotBlank()) onSave(newWalletName) },
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

package com.example.subz.ui.screens.manage

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import java.util.Date
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.R
import com.example.subz.data.local.dao.SubWithWallet
import com.example.subz.data.local.entity.WalletEntity
import com.example.subz.ui.components.SubzAlertDialog
import com.example.subz.ui.components.SubzButton
import com.example.subz.ui.components.SubzClickableField
import com.example.subz.ui.components.SubzTextField
import com.example.subz.ui.components.SubzTopAppBar
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.TextDarkNavy
import com.example.subz.ui.viewmodel.HomeViewModel
import com.example.subz.ui.viewmodel.WalletViewModel
import com.example.subz.utils.DateFormatter
import kotlinx.coroutines.flow.flowOf

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
            flowOf<SubWithWallet?>(null)
        }
    }.collectAsState(initial = null)

    val wallets by walletViewModel.wallets.collectAsState()
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var renewalDate by remember { mutableStateOf("") }

    var selectedWalletId by remember { mutableStateOf<Int?>(null) }
    var paymentMethodName by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf<String?>(null) }
    var priceError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }
    var walletError by remember { mutableStateOf<String?>(null) }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    var showWalletSelector by remember { mutableStateOf(false) }
    var showAddWalletDialog by remember { mutableStateOf(false) }
    var walletToDelete by remember { mutableStateOf<WalletEntity?>(null) }
    val context = LocalContext.current

    LaunchedEffect(subscriptionToEdit) {
        subscriptionToEdit?.let { data ->
            name = data.subscription.name
            price =
                if (data.subscription.price % 1.0 == 0.0) data.subscription.price.toInt().toString() else data.subscription.price.toString()
            renewalDate = data.subscription.renewalDate
            selectedWalletId = data.subscription.walletId
            paymentMethodName = data.walletName
        }
    }

    val errNameReq = stringResource(id = R.string.err_name_required)
    val errPriceReq = stringResource(id = R.string.err_price_required)
    val errPriceNeg = stringResource(id = R.string.err_price_negative)
    val errDateReq = stringResource(id = R.string.err_date_required)
    val errWalletReq = stringResource(id = R.string.err_wallet_required)

    val toastUpdated = stringResource(id = R.string.toast_sub_updated)
    val toastSaved = stringResource(id = R.string.toast_sub_saved)

    val deletedWalletName = walletToDelete?.name ?: ""
    val toastDeletedWallet = stringResource(id = R.string.toast_deleted, deletedWalletName)

    val isEditMode = subscriptionId != null
    val pageTitle = if (isEditMode) stringResource(id = R.string.edit_subscription) else stringResource(id = R.string.new_subscription)
    val buttonText = if (isEditMode) stringResource(id = R.string.update_changes) else stringResource(id = R.string.save_subscription)

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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SubzTextField(
                value = name,
                onValueChange = { name = it; nameError = null },
                label = stringResource(id = R.string.service_name),
                placeholder = stringResource(id = R.string.eg_netflix),
                isError = nameError != null,
                errorMessage = nameError
            )

            SubzTextField(
                value = price,
                onValueChange = { price = it; priceError = null },
                label = stringResource(id = R.string.price_rp),
                placeholder = stringResource(id = R.string.eg_price),
                keyboardType = KeyboardType.Number,
                isError = priceError != null,
                errorMessage = priceError
            )

            SubzClickableField(
                value = renewalDate,
                label = stringResource(id = R.string.renewal_trial_end),
                trailingIcon = Icons.Default.DateRange,
                onClick = { showDatePicker = true; dateError = null },
                isError = dateError != null,
                errorMessage = dateError
            )

            SubzClickableField(
                value = paymentMethodName,
                label = stringResource(id = R.string.payment_method),
                trailingIcon = Icons.Default.KeyboardArrowDown,
                onClick = { showWalletSelector = true; walletError = null },
                isError = walletError != null,
                errorMessage = walletError
            )
            Spacer(modifier = Modifier.weight(1f))

            SubzButton(
                text = buttonText,
                onClick = {
                    val parsedPrice = price.toDoubleOrNull()
                    var hasError = false

                    if (name.isBlank()) { nameError = errNameReq; hasError = true }
                    if (price.isBlank() || parsedPrice == null) { priceError = errPriceReq; hasError = true }
                    else if (parsedPrice < 0.0) { priceError = errPriceNeg; hasError = true }
                    if (renewalDate.isBlank()) { dateError = errDateReq; hasError = true }
                    if (selectedWalletId == null) { walletError = errWalletReq; hasError = true }

                    if (!hasError) {
                        if (isEditMode && subscriptionToEdit != null) {
                            viewModel.updateSubscription(
                                subscriptionToEdit!!.subscription.copy(
                                    name = name.trim(),
                                    price = parsedPrice!!,
                                    renewalDate = renewalDate.trim(),
                                    walletId = selectedWalletId!!
                                )
                            )
                            Toast.makeText(context, toastUpdated, Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addSubscription(
                                name = name.trim(),
                                price = parsedPrice!!,
                                renewalDate = renewalDate.trim(),
                                walletId = selectedWalletId!!
                            )
                            Toast.makeText(context, toastSaved, Toast.LENGTH_SHORT).show()
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
                            renewalDate = DateFormatter.formatToDateOnly(Date(millis))
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
                onWalletSelected = { selectedWallet ->
                    selectedWalletId = selectedWallet.id
                    paymentMethodName = selectedWallet.name
                    showWalletSelector = false
                },
                onDeleteWallet = { walletToDelete = it },
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
                    walletViewModel.addWallet(newName.trim())
                    showAddWalletDialog = false
                    showWalletSelector = true
                }
            )
        }

        if (walletToDelete != null) {
            SubzAlertDialog(
                title = stringResource(id = R.string.delete_wallet_title),
                message = stringResource(id = R.string.delete_wallet_msg, walletToDelete?.name ?: ""),
                confirmText = stringResource(id = R.string.delete),
                isDestructive = true,
                onConfirm = {
                    walletToDelete?.let {
                        walletViewModel.deleteWallet(it)
                        Toast.makeText(context, toastDeletedWallet, Toast.LENGTH_SHORT).show()
                    }
                    walletToDelete = null
                    paymentMethodName = ""
                    selectedWalletId = null
                },
                onDismiss = { walletToDelete = null}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WalletSelectorSheet(
    wallets: List<WalletEntity>,
    onDismiss: () -> Unit,
    onWalletSelected: (WalletEntity) -> Unit,
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
            text = stringResource(id = R.string.select_payment_method),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )
            if (wallets.isEmpty()) {
                Text(stringResource(id = R.string.no_wallets_added), color = Color.Gray, modifier = Modifier.padding(vertical = 16.dp))
            } else {
                LazyColumn {
                    items(wallets) { wallet ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onWalletSelected(wallet) }
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
                Text(stringResource(id = R.string.add_new_wallet), color = PrimaryBlue)
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
                text = stringResource(id = R.string.add_new_wallet_title),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
            SubzTextField(
                value = newWalletName,
                onValueChange = { newWalletName = it },
                label = stringResource(id = R.string.wallet_name),
                placeholder = stringResource(id = R.string.eg_wallet)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(id = R.string.cancel), color = TextDarkNavy)
                }
                Spacer(modifier = Modifier.width(8.dp))
                SubzButton(
                    text = stringResource(id = R.string.save),
                    onClick = { if (newWalletName.isNotBlank()) onSave(newWalletName) },
                    modifier = Modifier.width(120.dp)
                )
            }
        }
    }
}

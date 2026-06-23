package com.example.subz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subz.data.local.entity.WalletEntity
import com.example.subz.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository
) : ViewModel() {
    val wallets: StateFlow<List<WalletEntity>> =
        walletRepository.getAllWallets()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addWallet(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.insertWallet(WalletEntity(name = name))
        }
    }

    fun deleteWallet(wallet: WalletEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            walletRepository.deleteWallet(wallet)
        }
    }
}
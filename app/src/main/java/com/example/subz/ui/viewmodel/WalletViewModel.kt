package com.example.subz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subz.data.local.dao.WalletDao
import com.example.subz.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class WalletViewModel @Inject constructor(
    private val walletDao: WalletDao
) : ViewModel() {
    val wallets: StateFlow<List<WalletEntity>> =
        walletDao.getAllWallets()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    fun addWallet(name: String) {
        viewModelScope.launch {
            walletDao.insertWallet(WalletEntity(name = name))
        }
    }

    fun deleteWallet(wallet: WalletEntity) {
        viewModelScope.launch {
            walletDao.deleteWallet(wallet)
        }
    }
}
package com.example.subz.domain.repository

import com.example.subz.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    fun getAllWallets(userId: String): Flow<List<WalletEntity>>
    fun insertWallet(wallet: WalletEntity)
    fun deleteWallet(wallet: WalletEntity)
}
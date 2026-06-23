package com.example.subz.data.repository

import com.example.subz.data.local.dao.WalletDao
import com.example.subz.data.local.entity.WalletEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepository @Inject constructor(
    private val walletDao: WalletDao
) {
    fun getAllWallets(): Flow<List<WalletEntity>> {
        return walletDao.getAllWallets()
    }

    fun insertWallet(wallet: WalletEntity) {
        walletDao.insertWallet(wallet)
    }

    fun deleteWallet(wallet: WalletEntity) {
        walletDao.deleteWallet(wallet)
    }
}
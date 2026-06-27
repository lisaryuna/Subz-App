package com.example.subz.data.repository

import com.example.subz.data.local.dao.WalletDao
import com.example.subz.data.local.entity.WalletEntity
import com.example.subz.domain.repository.WalletRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val walletDao: WalletDao
): WalletRepository {
    override fun getAllWallets(userId: String): Flow<List<WalletEntity>> {
        return walletDao.getAllWallets(userId)
    }

    override fun insertWallet(wallet: WalletEntity) {
        walletDao.insertWallet(wallet)
    }

    override fun deleteWallet(wallet: WalletEntity) {
        walletDao.deleteWallet(wallet)
    }
}
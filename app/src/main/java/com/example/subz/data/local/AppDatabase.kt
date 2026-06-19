package com.example.subz.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.data.local.dao.WalletDao
import com.example.subz.data.local.entity.SubscriptionEntity
import com.example.subz.data.local.entity.WalletEntity

@Database(
    entities = [SubscriptionEntity::class, WalletEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
    abstract fun walletDao(): WalletDao
}
package com.example.subz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.subz.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

data class SubWithWallet(
    @Embedded val subscription: SubscriptionEntity,
    val walletName: String
)
@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubscription(subscription: SubscriptionEntity)

    @Update
    fun updateSubscription(subscription: SubscriptionEntity)

    @Delete
    fun deleteSubscription(subscription: SubscriptionEntity)

    @Query("""
        SELECT subscriptions.*, wallets.name AS walletName
        FROM subscriptions INNER JOIN wallets on subscriptions.walletId = wallets.id
        ORDER BY subscriptions.renewalDate ASC""")
    fun getAllSubscriptions(): Flow<List<SubWithWallet>>

    @Query("SELECT SUM(price) FROM subscriptions")
    fun getTotalActiveSubscriptions(): Flow<Double?>

    @Query("""
        SELECT subscriptions.*, wallets.name AS walletName 
        FROM subscriptions INNER JOIN wallets ON subscriptions.walletId = wallets.id 
        WHERE subscriptions.id = :id""")
    fun getSubscriptionById(id: Int): Flow<SubWithWallet?>

    @JvmSuppressWildcards
    @Query("""
        SELECT subscriptions.*, wallets.name AS walletName
        FROM subscriptions INNER JOIN wallets ON subscriptions.walletId = wallets.id""")
    suspend fun getAllSubscriptionsOneShot(): List<SubWithWallet>
}
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
        WHERE subscriptions.userId = :userId
        ORDER BY subscriptions.renewalDate ASC""")
    fun getAllSubscriptions(userId: String): Flow<List<SubWithWallet>>

    @Query("SELECT SUM(price) FROM subscriptions WHERE userId = :userId")
    fun getTotalActiveSubscriptions(userId: String): Flow<Double?>

    @Query("""
        SELECT subscriptions.*, wallets.name AS walletName 
        FROM subscriptions INNER JOIN wallets ON subscriptions.walletId = wallets.id 
        WHERE subscriptions.id = :id""")
    fun getSubscriptionById(id: Int): Flow<SubWithWallet?>

    @JvmSuppressWildcards
    @Query("""
        SELECT subscriptions.*, wallets.name AS walletName
        FROM subscriptions INNER JOIN wallets ON subscriptions.walletId = wallets.id
        WHERE subscriptions.userId = :userId""")
    suspend fun getAllSubscriptionsOneShot(userId: String): List<SubWithWallet>

    @JvmSuppressWildcards
    @Query("""
        SELECT subscriptions.*, wallets.name AS walletName
        FROM subscriptions INNER JOIN wallets ON subscriptions.walletId = wallets.id
        WHERE subscriptions.renewalDate = :date""")
    suspend fun getSubscriptionsByDateOneShot(date: String): List<SubWithWallet>
}
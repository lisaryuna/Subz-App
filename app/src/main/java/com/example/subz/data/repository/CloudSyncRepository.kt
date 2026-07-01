package com.example.subz.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.data.local.dao.WalletDao
import com.example.subz.data.local.entity.SubscriptionEntity
import com.example.subz.data.local.entity.WalletEntity
import com.example.subz.utils.DateFormatter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
    private val walletDao: WalletDao,
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    @param:ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("subz_pref", Context.MODE_PRIVATE)

    companion object {
        private const val NODE_USERS = "users"
        private const val NODE_WALLETS = "wallets"
        private const val NODE_SUBSCRIPTIONS = "subscriptions"
    }

    fun getLastSyncTime(): String {
        return prefs.getString("last_sync_time", "Never") ?: "Never"
    }

    fun isAutoSyncEnabled(): Boolean {
        return prefs.getBoolean("auto_sync_enabled", true)
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("auto_sync_enabled", enabled)}
    }

    suspend fun backupDataToCloud(): Result<String> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        val userId = user.uid

        return try {
            val wallets = walletDao.getAllWalletsOneShot(userId)
            val walletsRef = database.reference.child(NODE_USERS).child(userId).child(NODE_WALLETS)
            val walletUpdates = mutableMapOf<String, Any>()
            wallets.forEach { wallet ->
                walletUpdates[wallet.id.toString()] = wallet
            }
            walletsRef.setValue(walletUpdates).await()

            val subscriptions = subscriptionDao.getAllSubscriptionsOneShot(userId)
            val subsRef = database.reference.child(NODE_USERS).child(userId).child(NODE_SUBSCRIPTIONS)
            val subsUpdates = mutableMapOf<String, Any>()
            subscriptions.forEach { sub ->
                subsUpdates[sub.subscription.id.toString()] = sub.subscription
            }
            subsRef.setValue(subsUpdates).await()

            val currentTime = DateFormatter.formatToDateTime(Date())
            prefs.edit { putString("last_sync_time", currentTime) }

            Result.success(currentTime)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreDataFromCloud(): Result<Unit> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        val userId = user.uid

        return try {
            val walletsSnapshot = database.reference.child(NODE_USERS).child(userId).child(NODE_WALLETS).get().await()
            walletsSnapshot.children.forEach { child ->
                val wallet = child.getValue(WalletEntity::class.java)
                if (wallet != null) walletDao.insertWallet(wallet.copy(userId = userId))
            }

            val subsSnapshot = database.reference.child(NODE_USERS).child(userId).child(NODE_SUBSCRIPTIONS).get().await()
            subsSnapshot.children.forEach { child ->
                val sub = child.getValue(SubscriptionEntity::class.java)
                if (sub != null) subscriptionDao.insertSubscription(sub.copy(userId = userId))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
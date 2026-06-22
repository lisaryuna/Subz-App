package com.example.subz.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.data.local.dao.WalletDao
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

    fun getLastSyncTime(): String {
        return prefs.getString("last_sync_time", "Never") ?: "Never"
    }

    fun isAutoSyncEnabled(): Boolean {
        return prefs.getBoolean("auto_sync_enabled", false)
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        prefs.edit { putBoolean("auto_sync_enabled", enabled)}
    }

    suspend fun backupDataToCloud(): Result<String> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        val userId = user.uid

        return try {
            val wallets = walletDao.getAllWalletsOneShot()
            val walletsRef = database.reference.child("users").child(userId).child("wallets")
            val walletUpdates = mutableMapOf<String, Any>()
            wallets.forEach { wallet ->
                walletUpdates[wallet.id.toString()] = wallet
            }
            walletsRef.setValue(walletUpdates).await()

            val subscriptions = subscriptionDao.getAllSubscriptionsOneShot()
            val subsRef = database.reference.child("users").child(userId).child("subscriptions")
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
}
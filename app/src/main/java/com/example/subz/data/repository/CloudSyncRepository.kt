package com.example.subz.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.subz.data.local.dao.SubscriptionDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudSyncRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao,
    private val database: FirebaseDatabase,
    private val auth: FirebaseAuth,
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("subz_pref", Context.MODE_PRIVATE)

    fun getLastSyncTime(): String {
        return prefs.getString("last_sync_time", "Never") ?: "Never"
    }

    fun isAutoSyncEnabled(): Boolean {
        return prefs.getBoolean("auto_sync_enabled", false)
    }

    fun setAutoSyncEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("auto_sync_enabled", enabled).apply()
    }

    suspend fun backupDataToCloud(): Result<String> {
        val user = auth.currentUser ?: return Result.failure(Exception("User not logged in"))
        val userId = user.uid

        return try {
            val subscriptions = subscriptionDao.getAllSubscriptionsOneShot()
            val userRef = database.reference.child("users").child(userId).child("subscriptions")
            val updates = mutableMapOf<String, Any>()
            subscriptions.forEach { sub ->
                updates[sub.id.toString()] = sub
            }

            userRef.setValue(updates).await()

            val currentTime = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
            prefs.edit().putString("last_sync_time", currentTime).apply()

            Result.success(currentTime)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
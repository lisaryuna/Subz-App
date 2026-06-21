package com.example.subz.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.subz.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SubscriptionDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    fun insertSubscription(subscription: SubscriptionEntity)

    @Update
    fun updateSubscription(subscription: SubscriptionEntity)

    @Delete
    fun deleteSubscription(subscription: SubscriptionEntity)

    @Query("SELECT * FROM subscriptions ORDER BY renewalDate ASC")
    fun getAllSubscriptions(): Flow<List<SubscriptionEntity>>

    @Query("SELECT SUM(price) FROM subscriptions")
    fun getTotalActiveSubscriptions(): Flow<Double?>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    fun getSubscriptionById(id: Int): Flow<SubscriptionEntity?>

    @Query("SELECT * FROM subscriptions")
    suspend fun getAllSubscriptionsOneShot(): List<SubscriptionEntity>
}
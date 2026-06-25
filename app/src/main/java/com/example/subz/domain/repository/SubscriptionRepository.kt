package com.example.subz.domain.repository

import com.example.subz.data.local.dao.SubWithWallet
import com.example.subz.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

interface SubscriptionRepository {
    fun getAllSubscriptions(): Flow<List<SubWithWallet>>
    fun getTotalActiveSubscriptions(): Flow<Double?>
    fun getSubscriptionById(id: Int): Flow<SubWithWallet?>
    fun insertSubscription(subscription: SubscriptionEntity)
    fun updateSubscription(subscription: SubscriptionEntity)
    fun deleteSubscription(subscription: SubscriptionEntity)
}
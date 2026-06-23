package com.example.subz.data.repository

import com.example.subz.data.local.dao.SubWithWallet
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepository @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) {
    fun getAllSubscriptions(): Flow<List<SubWithWallet>> {
        return subscriptionDao.getAllSubscriptions()
    }

    fun getTotalActiveSubscriptions(): Flow<Double?> {
        return subscriptionDao.getTotalActiveSubscriptions()
    }

    fun getSubscriptionById(id: Int): Flow<SubWithWallet?> {
        return subscriptionDao.getSubscriptionById(id)
    }

    fun insertSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.insertSubscription(subscription)
    }

    fun updateSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.updateSubscription(subscription)
    }

    fun deleteSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.deleteSubscription(subscription)
    }
}
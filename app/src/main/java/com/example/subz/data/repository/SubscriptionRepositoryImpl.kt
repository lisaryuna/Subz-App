package com.example.subz.data.repository

import com.example.subz.data.local.dao.SubWithWallet
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.data.local.entity.SubscriptionEntity
import com.example.subz.domain.repository.SubscriptionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SubscriptionRepositoryImpl @Inject constructor(
    private val subscriptionDao: SubscriptionDao
): SubscriptionRepository {
    override fun getAllSubscriptions(userId: String): Flow<List<SubWithWallet>> {
        return subscriptionDao.getAllSubscriptions(userId)
    }

    override fun getTotalActiveSubscriptions(userId: String): Flow<Double?> {
        return subscriptionDao.getTotalActiveSubscriptions(userId)
    }

    override fun getSubscriptionById(id: Int): Flow<SubWithWallet?> {
        return subscriptionDao.getSubscriptionById(id)
    }

    override fun insertSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.insertSubscription(subscription)
    }

    override fun updateSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.updateSubscription(subscription)
    }

    override fun deleteSubscription(subscription: SubscriptionEntity) {
        subscriptionDao.deleteSubscription(subscription)
    }
}
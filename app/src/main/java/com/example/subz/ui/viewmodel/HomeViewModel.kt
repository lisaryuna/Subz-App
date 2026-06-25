package com.example.subz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subz.data.local.dao.SubWithWallet
import com.example.subz.data.local.entity.SubscriptionEntity
import com.example.subz.data.repository.CloudSyncRepository
import com.example.subz.domain.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val cloudSyncRepository: CloudSyncRepository
) : ViewModel() {
    val subscriptions: StateFlow<List<SubWithWallet>> =
        subscriptionRepository.getAllSubscriptions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val totalActivePrice: StateFlow<Double> =
        subscriptionRepository.getTotalActiveSubscriptions()
            .map { it ?: 0.0}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )

    private suspend fun triggerAutoSync() {
        if (cloudSyncRepository.isAutoSyncEnabled()) {
            cloudSyncRepository.backupDataToCloud()
        }
    }

    fun addSubscription(name: String, price: Double, renewalDate: String, walletId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val newSubscription = SubscriptionEntity(
                name = name,
                price = price,
                renewalDate = renewalDate,
                walletId = walletId
            )
            subscriptionRepository.insertSubscription(newSubscription)
            triggerAutoSync()
        }
    }

    fun updateSubscription(subscriptionEntity: SubscriptionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            subscriptionRepository.updateSubscription(subscriptionEntity)
            triggerAutoSync()
        }
    }

    fun deleteSubscription(subscriptionEntity: SubscriptionEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            subscriptionRepository.deleteSubscription(subscriptionEntity)
            triggerAutoSync()
        }
    }

    fun getSubscriptionById(id: Int): Flow<SubWithWallet?> {
        return subscriptionRepository.getSubscriptionById(id)
    }
}
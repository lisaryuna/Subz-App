package com.example.subz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.data.local.entity.SubscriptionEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val subscriptionDao: SubscriptionDao
) : ViewModel() {
    val subscriptions: StateFlow<List<SubscriptionEntity>> =
        subscriptionDao.getAllSubscriptions()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val totalActivePrice: StateFlow<Double> =
        subscriptionDao.getTotalActiveSubscriptions()
            .map { it ?: 0.0}
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0.0
            )

    fun addSubscription(name: String, price: Double, renewalDate: String, paymentMethod: String) {
        viewModelScope.launch {
            val newSubscription = SubscriptionEntity(
                name = name,
                price = price,
                renewalDate = renewalDate,
                paymentMethod = paymentMethod
            )
            subscriptionDao.insertSubscription(newSubscription)
        }
    }

    fun updateSubscription(subscriptionEntity: SubscriptionEntity) {
        viewModelScope.launch {
            subscriptionDao.updateSubscription(subscriptionEntity)
        }
    }

    fun deleteSubscription(subscriptionEntity: SubscriptionEntity) {
        viewModelScope.launch {
            subscriptionDao.deleteSubscription(subscriptionEntity)
        }
    }
}
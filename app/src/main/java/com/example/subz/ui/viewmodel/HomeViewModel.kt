package com.example.subz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subz.data.local.dao.SubWithWallet
import com.example.subz.data.local.entity.SubscriptionEntity
import com.example.subz.data.repository.CloudSyncRepository
import com.example.subz.domain.repository.SubscriptionRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface UiState<out T> {
    object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val subscriptionRepository: SubscriptionRepository,
    private val cloudSyncRepository: CloudSyncRepository
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val currentUserId = auth.currentUser?.uid ?: ""
    val subscriptionsState: StateFlow<UiState<List<SubWithWallet>>> =
        subscriptionRepository.getAllSubscriptions(currentUserId)
            .map { UiState.Success(it) as UiState<List<SubWithWallet>> }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = UiState.Loading
            )

    val totalActivePrice: StateFlow<Double> =
        subscriptionRepository.getTotalActiveSubscriptions(currentUserId)
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
                walletId = walletId,
                userId = currentUserId
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
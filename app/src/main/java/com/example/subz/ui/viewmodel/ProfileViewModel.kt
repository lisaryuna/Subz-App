package com.example.subz.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subz.data.repository.CloudSyncRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.concurrent.timer

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val syncRepository: CloudSyncRepository
) : ViewModel() {
    private val _lastSyncTime = MutableStateFlow(syncRepository.getLastSyncTime())
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _isSyncEnabled = MutableStateFlow(syncRepository.isAutoSyncEnabled())
    val isSyncEnabled: StateFlow<Boolean> = _isSyncEnabled.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    fun toggleSync(enabled: Boolean) {
        syncRepository.setAutoSyncEnabled(enabled)
        _isSyncEnabled.value = enabled

        if (enabled) {
            performManualSync()
        }
    }

    private fun performManualSync() {
        viewModelScope.launch {
            _isSyncing.value = true
            val result = syncRepository.backupDataToCloud()
            result.onSuccess { time ->
                _lastSyncTime.value = time
            }
        }
        _isSyncing.value = false
    }
}
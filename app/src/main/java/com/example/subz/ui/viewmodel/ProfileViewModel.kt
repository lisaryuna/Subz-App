package com.example.subz.ui.viewmodel

import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.subz.data.repository.CloudSyncRepository
import com.example.subz.worker.BillReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val syncRepository: CloudSyncRepository,
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _lastSyncTime = MutableStateFlow(syncRepository.getLastSyncTime())
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _isSyncEnabled = MutableStateFlow(syncRepository.isAutoSyncEnabled())
    val isSyncEnabled: StateFlow<Boolean> = _isSyncEnabled.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val prefs = context.getSharedPreferences("subz_pref", Context.MODE_PRIVATE)
    private val _isReminderEnabled = MutableStateFlow(prefs.getBoolean("reminder_enabled", true))
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled.asStateFlow()

    fun toggleSync(enabled: Boolean) {
        syncRepository.setAutoSyncEnabled(enabled)
        _isSyncEnabled.value = enabled

        if (enabled) {
            performManualSync()
        }
    }

    fun toggleReminder(enabled: Boolean) {
        prefs.edit { putBoolean("reminder_enabled", enabled)}
        _isReminderEnabled.value = enabled

        val workManager = WorkManager.getInstance(context)
        if (enabled) {
            val reminderRequest = PeriodicWorkRequestBuilder<BillReminderWorker>(
                24, TimeUnit.HOURS
            ).build()
            workManager.enqueueUniquePeriodicWork(
                "SubzBillReminderWork",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderRequest
            )
        } else {
            workManager.cancelUniqueWork("SubzBillReminderWork")
        }
    }

    fun triggerDemoReminder() {
        val workManager = WorkManager.getInstance(context)
        val demoRequest = OneTimeWorkRequestBuilder<BillReminderWorker>()
            .setInitialDelay(3, TimeUnit.SECONDS)
            .build()

        workManager.enqueueUniqueWork(
            "DemoSubzWork",
            ExistingWorkPolicy.REPLACE,
            demoRequest
        )
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
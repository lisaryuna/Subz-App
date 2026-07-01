package com.example.subz

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.subz.ui.MainScreen
import com.example.subz.ui.theme.SubzTheme
import com.example.subz.worker.BillReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val requestPermissonLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            scheduleBillReminder()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        askNotificationPermission()
        setContent {
            SubzTheme {
                MainScreen()
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                scheduleBillReminder()
            } else {
                requestPermissonLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            scheduleBillReminder()
        }
    }

    private fun scheduleBillReminder() {
        val prefs = getSharedPreferences("subz_pref", MODE_PRIVATE)
        val isReminderEnabled = prefs.getBoolean("reminder_enabled", true)

        if (isReminderEnabled) {
            val delay = calculateInitialDelayTo8AM()

            val reminderRequest = PeriodicWorkRequestBuilder<BillReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
                "SubzBillReminderWork",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderRequest
            )
        }
    }

    private fun calculateInitialDelayTo8AM(): Long {
        val currentTime = System.currentTimeMillis()
        val calendar = getInstance().apply {
            timeInMillis = currentTime
            set(HOUR_OF_DAY, 8)
            set(MINUTE, 0)
            set(SECOND, 0)
            set(MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= currentTime) {
            calendar.add(DAY_OF_YEAR, 1)
        }

        return calendar.timeInMillis - currentTime
    }
}
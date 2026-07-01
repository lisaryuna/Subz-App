package com.example.subz.worker

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.Calendar.*
import java.util.concurrent.TimeUnit

object ReminderManager {
    fun scheduleOrCancelReminder(context: Context, isEnabled: Boolean) {
        val workManager = WorkManager.getInstance(context)

        if (isEnabled) {
            val delay = calculateInitialDelayTo8AM()
            val reminderRequest = PeriodicWorkRequestBuilder<BillReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            workManager.enqueueUniquePeriodicWork(
                "SubzBillReminderWork",
                ExistingPeriodicWorkPolicy.KEEP,
                reminderRequest
            )
        } else {
            workManager.cancelUniqueWork("SubzBillReminderWork")
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
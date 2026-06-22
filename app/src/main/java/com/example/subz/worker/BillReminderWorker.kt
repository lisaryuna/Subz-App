package com.example.subz.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.subz.R
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.utils.DateFormatter
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar

@HiltWorker
class BillReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val subscriptionDao: SubscriptionDao
    ) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val subscriptions = subscriptionDao.getAllSubscriptionsOneShot()
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowDate = DateFormatter.formatToDateOnly(calendar.time)

        subscriptions.forEach { sub ->
            if (sub.renewalDate == tomorrowDate) {
                showNotification(sub.name, sub.price)
            }
        }

        return Result.success()
    }

    private fun showNotification(serviceName: String, price: Double) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "bill_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel (
                channelId,
                "Bill Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Billing Reminder!")
            .setContentText("Heads up! Your $serviceName subscription (Rp $price) renews tomorrow. Keep it or cancel it?")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(serviceName.hashCode(), notification)
    }
}
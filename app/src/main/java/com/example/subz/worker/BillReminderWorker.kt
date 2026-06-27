package com.example.subz.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.subz.R
import com.example.subz.data.local.dao.SubscriptionDao
import com.example.subz.utils.CurrencyFormatter
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
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val tomorrowDate = DateFormatter.formatToDateOnly(calendar.time)

        val tomorrowSubscriptions = subscriptionDao.getSubscriptionsByDateOneShot(tomorrowDate)

        tomorrowSubscriptions.forEach { sub ->
            showNotification(sub.subscription.name, sub.subscription.price, sub.subscription.id)
        }

        return Result.success()
    }

    private fun showNotification(serviceName: String, price: Double, notificationId: Int) {
        val formattedPrice = CurrencyFormatter.formatRupiah(price)
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

        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("subz://detail/$notificationId")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Billing Reminder!")
            .setContentText("Heads up! Your $serviceName subscription ($formattedPrice) renews tomorrow. Keep it or cancel it?")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}
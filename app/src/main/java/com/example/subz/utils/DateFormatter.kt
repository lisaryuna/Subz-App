package com.example.subz.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateFormatter {
    private val defaultLocale = Locale("id", "ID")

    fun formatToDateOnly(date: Date): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd", defaultLocale)
        return formatter.format(date)
    }

    fun formatForUI(dateString: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", defaultLocale)
            val formatter = SimpleDateFormat("dd MMM yyyy", defaultLocale)
            val date = parser.parse(dateString)
            if (date != null) formatter.format(date) else dateString
        } catch (_: Exception) {
            dateString
        }
    }

    fun formatToDateTime(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", defaultLocale)
        return formatter.format(date)
    }
}
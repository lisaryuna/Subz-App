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

    fun formatToDateTime(date: Date): String {
        val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", defaultLocale)
        return formatter.format(date)
    }
}
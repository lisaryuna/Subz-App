package com.example.subz.utils

import java.text.NumberFormat
import java.util.Locale

object CurrencyFormatter {
    fun formatRupiah(amount: Double): String {
        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
        return "RP ${format.format(amount)}"
    }
}
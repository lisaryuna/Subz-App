package com.example.subz.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.TextDarkNavy
import com.example.subz.R
import com.example.subz.data.local.dao.SubWithWallet
import com.example.subz.utils.CurrencyFormatter


fun getServiceIcon(name: String): Int? {
    val lowerName = name.lowercase()
    return when {
        lowerName.contains("apple") -> R.drawable.ic_apple_music
        lowerName.contains("canva") -> R.drawable.ic_canva
        lowerName.contains("capcut") -> R.drawable.ic_capcut
        lowerName.contains("chatgpt") || lowerName.contains("openai") -> R.drawable.ic_chatgpt
        lowerName.contains("discord") -> R.drawable.ic_discord
        lowerName.contains("disney") || lowerName.contains("hotstar") -> R.drawable.ic_disney
        lowerName.contains("duolingo") -> R.drawable.ic_duolingo
        lowerName.contains("icloud") -> R.drawable.ic_icloud
        lowerName.contains("joox") -> R.drawable.ic_joox
        lowerName.contains("microsoft") || lowerName.contains("office") -> R.drawable.ic_microsoft365
        lowerName.contains("netflix") -> R.drawable.ic_netflix
        lowerName.contains("notion") -> R.drawable.ic_notion
        lowerName.contains("prime") -> R.drawable.ic_prime
        lowerName.contains("spotify") -> R.drawable.ic_spotify
        lowerName.contains("steam") -> R.drawable.ic_steam
        lowerName.contains("vidio") -> R.drawable.ic_vidio
        lowerName.contains("viu") -> R.drawable.ic_viu
        lowerName.contains("wetv") -> R.drawable.ic_wetv
        lowerName.contains("youtube") -> R.drawable.ic_youtube
        lowerName.contains("zoom") -> R.drawable.ic_zoom
        else -> null
    }
}

@Composable
fun SubscriptionItem(
    item: SubWithWallet,
    onClick: () -> Unit
) {
    val sub = item.subscription
    val paymentMethod = item.walletName

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val iconRes = getServiceIcon(sub.name)
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (iconRes != null) Color.White else PrimaryBlue,
                            shape = RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (iconRes != null) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = "${sub.name} Logo",
                            modifier = Modifier.size(36.dp)
                        )
                    } else {
                        Text(
                            text = sub.name.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = sub.name,
                        color = TextDarkNavy,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = paymentMethod,
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.formatRupiah(sub.price),
                    color = PrimaryBlue,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DateRange,
                        contentDescription = "Date",
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = sub.renewalDate,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
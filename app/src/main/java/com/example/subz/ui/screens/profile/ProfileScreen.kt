package com.example.subz.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.subz.R
import com.example.subz.ui.components.SubzAlertDialog
import com.example.subz.ui.components.SubzTopAppBar
import com.example.subz.ui.theme.BackgroundLight
import com.example.subz.ui.theme.IndicatorLightBlue
import com.example.subz.ui.theme.PrimaryBlue
import com.example.subz.ui.theme.TextDarkNavy
import com.example.subz.ui.viewmodel.AuthViewModel
import com.example.subz.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit
) {
    val currentUser = authViewModel.currentUser
    val displayName = currentUser?.displayName ?: "User"
    val email = currentUser?.email ?: "No Email"
    val initials = getInitials(displayName)

    val lastSyncTime by profileViewModel.lastSyncTime.collectAsState()
    val isCloudSyncEnabled by profileViewModel.isSyncEnabled.collectAsState()
    val isSyncing by profileViewModel.isSyncing.collectAsState()
    val isReminderEnabled by profileViewModel.isReminderEnabled.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            SubzTopAppBar(title = stringResource(id = R.string.app_name))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .padding(top = 8.dp, bottom = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(IndicatorLightBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkNavy
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkNavy
            )
            Text(
                text = email,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    SettingsRow(
                        icon = Icons.Outlined.Cloud,
                        title = stringResource(id = R.string.cloud_sync),
                        subtitle = if (isSyncing) stringResource(id = R.string.syncing) else stringResource(id = R.string.last_backed_up, lastSyncTime),
                        content = {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = PrimaryBlue,
                                    strokeWidth = 2.dp)
                            } else {
                                Switch(
                                    checked = isCloudSyncEnabled,
                                    onCheckedChange = { profileViewModel.toggleSync(it) },
                                    colors = SwitchDefaults.colors(
                                        checkedTrackColor = PrimaryBlue,
                                        uncheckedTrackColor = Color.LightGray
                                    )
                                )
                            }
                        }
                    )
                    HorizontalDivider(color = BackgroundLight)
                    SettingsRow(
                        icon = Icons.Outlined.Notifications,
                        title = stringResource(id = R.string.bill_reminder),
                        content = {
                            Switch(
                                checked = isReminderEnabled,
                                onCheckedChange = { profileViewModel.toggleReminder(it) },
                                colors = SwitchDefaults.colors(
                                    checkedTrackColor = PrimaryBlue,
                                    uncheckedTrackColor = Color.LightGray
                                )
                            )
                        }
                    )
                    HorizontalDivider(color = BackgroundLight)

                    SettingsRow(
                        icon = Icons.Outlined.Info,
                        title = stringResource(id = R.string.version),
                        content = {
                            Text("v1.0.0", color = Color.Gray, fontSize = 14.sp)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, TextDarkNavy),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDarkNavy)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = "Logout",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(id = R.string.logout_title), fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        if (showLogoutDialog) {
            SubzAlertDialog(
                title = stringResource(id = R.string.logout_title),
                message = stringResource(id = R.string.logout_message),
                confirmText = stringResource(id = R.string.logout_title),
                isDestructive = false,
                onConfirm = {
                    showLogoutDialog = false
                    authViewModel.logout()
                    onNavigateToLogin()
                },
                onDismiss = { showLogoutDialog = false }
            )
        }
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(IndicatorLightBlue, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = title, tint = TextDarkNavy)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDarkNavy)
            if (subtitle != null) {
                Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
            }
        }
        content()
    }
}

fun getInitials(name: String): String {
    val words = name.trim().split(" ")
    if (words.isEmpty()) return "U"
    if (words.size == 1) return words[0].take(1).uppercase()
    return (words.first().take(1) + words.last().take(1)).uppercase()
}
package com.marcodomingues.noiseguard.ui.screens.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.marcodomingues.noiseguard.BuildConfig
import com.marcodomingues.noiseguard.ui.theme.DarkPremium
import com.marcodomingues.noiseguard.ui.theme.NeonColors
import com.marcodomingues.noiseguard.ui.theme.noiseGuardBackground

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val hasNotificationPermission = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    var showAboutDialog by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.historyCleared) {
        if (uiState.historyCleared) {
            snackbarHostState.showSnackbar("History cleared")
            viewModel.onHistoryClearedShown()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noiseGuardBackground()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Alerts section
            item {
                SettingsSection(title = "Alerts") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        NoiseLimitSlider(
                            value = uiState.noiseLimit,
                            onValueChange = { viewModel.updateNoiseLimit(it) }
                        )

                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Push Notifications",
                                    fontSize = 14.sp,
                                    color = Color.White
                                )
                                Switch(
                                    checked = uiState.pushNotificationsEnabled && hasNotificationPermission,
                                    onCheckedChange = { enabled ->
                                        if (!hasNotificationPermission) {
                                            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                            }
                                            context.startActivity(intent)
                                        } else {
                                            viewModel.togglePushNotifications(enabled)
                                        }
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = NeonColors.CyanStart,
                                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                        uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
                                    )
                                )
                            }
                            if (!hasNotificationPermission) {
                                Text(
                                    text = "Enable notifications in Android Settings",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.4f)
                                )
                            }
                        }

                        SettingToggle(
                            label = "Vibration on Alert",
                            checked = uiState.vibrationEnabled,
                            onCheckedChange = { viewModel.toggleVibration(it) }
                        )
                    }
                }
            }

            // Monitoring section
            item {
                SettingsSection(title = "Monitoring") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Sampling Rate",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.7f)
                        )

                        SamplingRateOptions(
                            selectedRate = uiState.samplingRate,
                            onRateSelected = { viewModel.updateSamplingRate(it) }
                        )

                        SettingToggle(
                            label = "Start monitoring on app open",
                            checked = uiState.autoStartEnabled,
                            onCheckedChange = { viewModel.toggleAutoStart(it) }
                        )
                    }
                }
            }

            // Appearance section
            item {
                SettingsSection(title = "Appearance") {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SettingToggle(
                            label = "Force Dark Mode",
                            checked = uiState.forceDarkMode,
                            onCheckedChange = { viewModel.toggleForceDarkMode(it) }
                        )
                    }
                }
            }

            // Actions section
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActionButton(
                        text = "About NoiseGuard",
                        icon = "📖",
                        onClick = { showAboutDialog = true }
                    )

                    ActionButton(
                        text = "Clear History",
                        icon = "🗑️",
                        onClick = { showClearConfirmDialog = true },
                        isDestructive = true
                    )
                }
            }

            // Version info
            item {
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    if (showClearConfirmDialog) {
        ClearHistoryConfirmDialog(
            onConfirm = {
                showClearConfirmDialog = false
                viewModel.clearHistory()
            },
            onDismiss = { showClearConfirmDialog = false }
        )
    }
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "NoiseGuard",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Version ${BuildConfig.VERSION_NAME}",
                    fontSize = 13.sp,
                    color = NeonColors.CyanStart
                )
                Text(
                    text = "Real-time noise monitoring using your device microphone. No recordings stored - only decibel values.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = NeonColors.CyanStart)
            }
        },
        containerColor = DarkPremium.Surface,
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
private fun ClearHistoryConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete all history?",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        },
        text = {
            Text(
                text = "This cannot be undone.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Delete", color = DarkPremium.Destructive)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White.copy(alpha = 0.7f))
            }
        },
        containerColor = DarkPremium.Surface,
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = DarkPremium.Surface.copy(alpha = 0.5f)
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun NoiseLimitSlider(
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Noise Limit",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = "${value.toInt()} dB",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NeonColors.CyanStart
            )
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = 50f..90f,
            steps = 7,  // 5 dB steps across the 40 dB range
            colors = SliderDefaults.colors(
                thumbColor = NeonColors.CyanStart,
                activeTrackColor = NeonColors.CyanStart,
                inactiveTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun SettingToggle(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.White
        )

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = NeonColors.CyanStart,
                uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                uncheckedTrackColor = Color.White.copy(alpha = 0.2f)
            )
        )
    }
}

@Composable
private fun SamplingRateOptions(
    selectedRate: String,
    onRateSelected: (String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf(
            "Fast (50ms)" to "fast",
            "Normal (100ms)" to "normal",
            "Battery Saver (500ms)" to "battery"
        ).forEach { (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RadioButton(
                    selected = selectedRate == value,
                    onClick = { onRateSelected(value) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = NeonColors.CyanStart,
                        unselectedColor = Color.White.copy(alpha = 0.4f)
                    )
                )
                Text(
                    text = label,
                    fontSize = 14.sp,
                    color = if (selectedRate == value) Color.White else Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun ActionButton(
    text: String,
    icon: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .drawWithCache {
                val borderBrush = if (isDestructive) {
                    Brush.horizontalGradient(
                        colors = listOf(
                            DarkPremium.Destructive.copy(alpha = 0.5f),
                            Color(0xFFFF6F00).copy(alpha = 0.5f)
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        colors = listOf(
                            NeonColors.CyanStart.copy(alpha = 0.5f),
                            NeonColors.MagentaEnd.copy(alpha = 0.5f)
                        )
                    )
                }
                onDrawBehind {
                    drawRoundRect(
                        brush = borderBrush,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(28.dp.toPx()),
                        style = Stroke(width = 1.5.dp.toPx())
                    )
                }
            },
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = DarkPremium.Surface.copy(alpha = 0.3f),
            contentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = icon,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

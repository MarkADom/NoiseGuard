package com.marcodomingues.noiseguard.ui.screens.monitor

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.marcodomingues.noiseguard.ui.components.CircularGaugeMeter
import com.marcodomingues.noiseguard.ui.components.CurrentLevelIndicator
import com.marcodomingues.noiseguard.ui.components.DigitalDisplay
import com.marcodomingues.noiseguard.ui.theme.DarkPremium
import com.marcodomingues.noiseguard.ui.theme.NeonColors
import com.marcodomingues.noiseguard.ui.theme.noiseGuardBackground

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MonitorScreen(
    viewModel: MonitorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Haptic on category change while monitoring, not on initial composition
    LaunchedEffect(uiState.category) {
        if (uiState.isMonitoring) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    val recordAudioPermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    // POST_NOTIFICATIONS is only a runtime permission on API 33+.
    // Build.VERSION.SDK_INT is constant at runtime so this branch is stable across recompositions.
    @Suppress("NewApi")
    val notificationPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
    } else null

    Box(
        modifier = Modifier
            .fillMaxSize()
            .noiseGuardBackground()
    ) {
        when {
            // STATE 1: Microphone granted — show monitoring UI
            recordAudioPermission.status.isGranted -> {
                MonitoringContent(
                    uiState = uiState,
                    onStartStop = {
                        if (uiState.isMonitoring) {
                            viewModel.stopMonitoring()
                        } else {
                            // Request notification permission in context — only on first monitor start
                            notificationPermission?.let {
                                if (!it.status.isGranted) it.launchPermissionRequest()
                            }
                            viewModel.startMonitoring()
                        }
                    }
                )
            }

            // STATE 2: Denied once — OS will still show the system dialog; explain why first
            recordAudioPermission.status.shouldShowRationale -> {
                PermissionRationaleUI(
                    onRequestPermission = { recordAudioPermission.launchPermissionRequest() }
                )
            }

            // STATE 3: Permanently denied — system dialog suppressed; user must go to Settings
            else -> {
                PermissionPermanentlyDeniedUI(
                    onOpenSettings = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

// ── Private composables ────────────────────────────────────────────────────

@Composable
private fun MonitoringContent(
    uiState: MonitorUiState,
    onStartStop: () -> Unit
) {
    val categoryName = uiState.category.name

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Gauge and LED display share a Box so the number overlays the gauge centre
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularGaugeMeter(
                currentDb = uiState.currentDecibels,
                peakDb = uiState.peakToday
            )
            DigitalDisplay(
                value = uiState.currentDecibels.toInt()
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        CurrentLevelIndicator(
            value = uiState.currentDecibels.toInt(),
            category = categoryName
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onStartStop,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .drawWithCache {
                    val borderBrush = Brush.horizontalGradient(
                        colors = listOf(
                            NeonColors.CyanStart.copy(alpha = 0.5f),
                            NeonColors.MagentaEnd.copy(alpha = 0.5f)
                        )
                    )
                    onDrawBehind {
                        drawRoundRect(
                            brush = borderBrush,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(30.dp.toPx()),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                },
            shape = RoundedCornerShape(30.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkPremium.Surface.copy(alpha = 0.2f),
                contentColor = Color.White
            ),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
        ) {
            Icon(
                imageVector = if (uiState.isMonitoring) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = if (uiState.isMonitoring) "Stop monitoring" else "Start monitoring",
                tint = if (uiState.isMonitoring) NeonColors.MagentaEnd else NeonColors.CyanStart,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = if (uiState.isMonitoring) "STOP MONITORING" else "START MONITORING",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Shown after first denial — OS will still show the system dialog when the user taps.
 * Explains why the permission is needed before re-requesting.
 */
@Composable
private fun PermissionRationaleUI(
    onRequestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🎤", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Microphone Permission Required",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "NoiseGuard measures noise by analysing audio from your microphone. " +
                    "No recordings are stored — only decibel values.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkPremium.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonColors.CyanStart)
        ) {
            Text("Grant Permission", style = MaterialTheme.typography.titleMedium)
        }
    }
}

/**
 * Shown when the permission is permanently denied — the system dialog won't appear again.
 * Sends the user to the app's Settings page to grant it manually.
 */
@Composable
private fun PermissionPermanentlyDeniedUI(
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🚫", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Microphone Access Blocked",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The microphone permission was permanently denied. " +
                    "Open Settings and enable it under Permissions to use NoiseGuard.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkPremium.OnSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onOpenSettings,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NeonColors.MagentaEnd)
        ) {
            Text("Open Settings", style = MaterialTheme.typography.titleMedium)
        }
    }
}

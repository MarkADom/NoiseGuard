package com.marcodomingues.noiseguard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.marcodomingues.noiseguard.ui.navigation.NoiseGuardNavigation
import com.marcodomingues.noiseguard.ui.theme.NoiseGuardTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NoiseGuardTheme {
                NoiseGuardNavigation()
            }
        }
    }
}

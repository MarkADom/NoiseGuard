package com.marcodomingues.soundflow

import android.os.Bundle

import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import com.marcodomingues.soundflow.ui.navigation.SoundFlowNavigation
import com.marcodomingues.soundflow.ui.theme.SoundFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SoundFlowTheme {
                SoundFlowNavigation()
            }
        }
    }
}

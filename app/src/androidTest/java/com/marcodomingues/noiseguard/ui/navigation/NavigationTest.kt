package com.marcodomingues.noiseguard.ui.navigation

import android.Manifest
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.marcodomingues.noiseguard.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    // Grant RECORD_AUDIO before the activity starts so MonitorScreen renders its content,
    // not the permission rationale/blocked screen. POST_NOTIFICATIONS avoids the
    // notification dialog mid-test on API 33+.
    @get:Rule(order = 1)
    val grantPermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.POST_NOTIFICATIONS
    )

    @get:Rule(order = 2)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun appStartsOnMonitorScreen() {
        composeRule.onNodeWithText("START MONITORING").assertIsDisplayed()
    }

    @Test
    fun tapHistoryTab_showsHistoryScreen() {
        composeRule.onNodeWithText("History").performClick()
        composeRule.onNodeWithText("Last 24 Hours").assertIsDisplayed()
    }

    @Test
    fun tapSettingsTab_showsSettingsScreen() {
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("Noise Limit").assertIsDisplayed()
    }

    @Test
    fun tapMonitorTab_returnsToMonitorScreen() {
        composeRule.onNodeWithText("Settings").performClick()
        composeRule.onNodeWithText("Monitor").performClick()
        composeRule.onNodeWithText("START MONITORING").assertIsDisplayed()
    }
}

package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.example.data.model.CameraFilterConfig
import com.example.data.model.VideoFilterType
import com.example.ui.components.CameraColorMatrix
import com.example.ui.components.CameraFilterBottomSheet
import com.example.ui.theme.AiConnectTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CameraFilterTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `identity config produces null color filter and identity matrix`() {
        val defaultConfig = CameraFilterConfig()
        assertTrue(CameraColorMatrix.isIdentity(defaultConfig))
        assertNull(CameraColorMatrix.toAndroidColorFilter(defaultConfig))
        assertNull(CameraColorMatrix.toComposeColorFilter(defaultConfig))

        val values = CameraColorMatrix.buildColorMatrixArray(defaultConfig)
        assertEquals(20, values.size)
        assertEquals(1f, values[0], 0.001f)
        assertEquals(1f, values[6], 0.001f)
        assertEquals(1f, values[12], 0.001f)
        assertEquals(1f, values[18], 0.001f)
        assertEquals(0f, values[4], 0.001f)
    }

    @Test
    fun `all non-none presets produce non-identity color matrix`() {
        VideoFilterType.entries
            .filter { it != VideoFilterType.NONE }
            .forEach { preset ->
                val config = CameraFilterConfig(type = preset, intensity = 1.0f)
                assertFalse("Preset $preset should not be identity", CameraColorMatrix.isIdentity(config))
                assertNotNull(CameraColorMatrix.toAndroidColorFilter(config))
                assertNotNull(CameraColorMatrix.toComposeColorFilter(config))
            }
    }

    @Test
    fun `zero intensity returns identity even for active preset`() {
        val config = CameraFilterConfig(
            type = VideoFilterType.WARM_SUNSET,
            intensity = 0.0f
        )
        assertTrue(CameraColorMatrix.isIdentity(config))
        assertNull(CameraColorMatrix.toAndroidColorFilter(config))
    }

    @Test
    fun `brightness contrast and saturation adjustments mutate color matrix`() {
        val bright = CameraFilterConfig(brightness = 0.25f)
        val brightValues = CameraColorMatrix.buildColorMatrixArray(bright)
        assertTrue(brightValues[4] > 50f)

        val contrasted = CameraFilterConfig(contrast = 1.4f)
        val contrastValues = CameraColorMatrix.buildColorMatrixArray(contrasted)
        assertEquals(1.4f, contrastValues[0], 0.01f)

        val desaturated = CameraFilterConfig(saturation = 0.0f)
        val desatValues = CameraColorMatrix.buildColorMatrixArray(desaturated)
        assertEquals(desatValues[0], desatValues[5], 0.001f)
        assertEquals(desatValues[1], desatValues[6], 0.001f)
    }

    @Test
    fun `bottom sheet selects preset toggles inspector and applies filter`() {
        var appliedConfig: CameraFilterConfig? = null

        composeTestRule.setContent {
            AiConnectTheme {
                CameraFilterBottomSheet(
                    initialConfig = CameraFilterConfig(type = VideoFilterType.NONE),
                    isCameraOn = false,
                    isFrontCamera = true,
                    hasCameraPermission = false,
                    fallbackPreviewRes = R.drawable.avatar_nova,
                    onToggleFrontCamera = {},
                    onApplyFilter = { appliedConfig = it },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        // Verify live preview and preset row are displayed
        composeTestRule.onNodeWithTag("filter_live_preview").assertIsDisplayed()
        composeTestRule.onNodeWithTag("filter_presets_row").assertIsDisplayed()

        // Select Warm Sunset preset
        composeTestRule.onNodeWithTag("filter_preset_${VideoFilterType.WARM_SUNSET.id}")
            .performScrollTo()
            .performClick()
        composeTestRule.waitForIdle()

        // Toggle 4x5 Matrix Inspector and verify it appears
        composeTestRule.onNodeWithTag("toggle_matrix_inspector_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("matrix_inspector_card").assertIsDisplayed()
        composeTestRule.onNodeWithText("CameraX ColorMatrix (4×5)").assertIsDisplayed()

        // Toggle Before / After comparison
        composeTestRule.onNodeWithTag("compare_original_button").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("عرض الفلتر").assertIsDisplayed()

        // Apply filter and verify callback receives WARM_SUNSET
        composeTestRule.onNodeWithTag("apply_filter_button").performClick()
        composeTestRule.waitForIdle()

        assertNotNull(appliedConfig)
        assertEquals(VideoFilterType.WARM_SUNSET, appliedConfig?.type)
    }

    @Test
    fun `bottom sheet reset restores default normal config`() {
        var appliedConfig: CameraFilterConfig? = null

        composeTestRule.setContent {
            AiConnectTheme {
                CameraFilterBottomSheet(
                    initialConfig = CameraFilterConfig(
                        type = VideoFilterType.CYBER_GLOW,
                        intensity = 0.8f,
                        brightness = 0.2f,
                        contrast = 1.3f,
                        saturation = 1.5f
                    ),
                    isCameraOn = false,
                    isFrontCamera = true,
                    hasCameraPermission = false,
                    fallbackPreviewRes = R.drawable.avatar_nova,
                    onToggleFrontCamera = {},
                    onApplyFilter = { appliedConfig = it },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("reset_filter_button").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("apply_filter_button").performClick()
        composeTestRule.waitForIdle()

        assertEquals(CameraFilterConfig(), appliedConfig)
    }
}

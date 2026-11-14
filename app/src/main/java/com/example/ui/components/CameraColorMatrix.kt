package com.example.ui.components

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.compose.ui.graphics.ColorFilter
import com.example.data.model.CameraFilterConfig
import com.example.data.model.VideoFilterType
import kotlin.math.abs

/**
 * Standard 4x5 ColorMatrix builder and CameraX PreviewView real-time filter applicator.
 */
object CameraColorMatrix {

    private val IDENTITY_MATRIX = floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 1f, 0f
    )

    /**
     * Returns the base 20-element (4x5) color matrix for each preset filter type.
     */
    fun baseMatrixFor(type: VideoFilterType): FloatArray = when (type) {
        VideoFilterType.NONE -> IDENTITY_MATRIX.copyOf()

        VideoFilterType.CYBER_GLOW -> floatArrayOf(
            1.06f, 0.00f, 0.08f, 0f, -6f,
            0.00f, 1.04f, 0.12f, 0f, 4f,
            0.12f, 0.08f, 1.24f, 0f, 16f,
            0.00f, 0.00f, 0.00f, 1f, 0f
        )

        VideoFilterType.STUDIO_LIGHT -> floatArrayOf(
            1.08f, 0.02f, 0.00f, 0f, 14f,
            0.00f, 1.08f, 0.02f, 0f, 14f,
            0.02f, 0.00f, 1.10f, 0f, 16f,
            0.00f, 0.00f, 0.00f, 1f, 0f
        )

        VideoFilterType.WARM_SUNSET -> floatArrayOf(
            1.22f, 0.08f, 0.00f, 0f, 14f,
            0.04f, 1.06f, 0.00f, 0f, 4f,
            0.00f, 0.00f, 0.80f, 0f, -12f,
            0.00f, 0.00f, 0.00f, 1f, 0f
        )

        VideoFilterType.VIRTUAL_BLUR -> floatArrayOf(
            0.94f, 0.04f, 0.04f, 0f, -4f,
            0.02f, 0.96f, 0.04f, 0f, -2f,
            0.04f, 0.04f, 1.08f, 0f, 6f,
            0.00f, 0.00f, 0.00f, 1f, 0f
        )

        VideoFilterType.BEAUTY_SMOOTH -> floatArrayOf(
            1.08f, 0.04f, 0.02f, 0f, 12f,
            0.02f, 1.05f, 0.02f, 0f, 9f,
            0.02f, 0.02f, 1.02f, 0f, 8f,
            0.00f, 0.00f, 0.00f, 1f, 0f
        )

        VideoFilterType.VIVID_HDR -> floatArrayOf(
            1.26f, -0.06f, -0.04f, 0f, -10f,
            -0.04f, 1.24f, -0.04f, 0f, -10f,
            -0.04f, -0.06f, 1.28f, 0f, -10f,
            0.00f, 0.00f, 0.00f, 1f, 0f
        )

        VideoFilterType.NOIR_MONO -> floatArrayOf(
            0.36f, 0.70f, 0.14f, 0f, -18f,
            0.36f, 0.70f, 0.14f, 0f, -18f,
            0.38f, 0.72f, 0.16f, 0f, -14f,
            0.00f, 0.00f, 0.00f, 1f, 0f
        )
    }

    /**
     * Composes a complete 20-element 4x5 ColorMatrix array combining preset interpolation,
     * saturation, contrast, and brightness adjustments.
     */
    fun buildColorMatrixArray(config: CameraFilterConfig): FloatArray {
        val intensity = config.intensity.coerceIn(0f, 1f)
        val preset = baseMatrixFor(config.type)

        val blended = FloatArray(20) { index ->
            val idVal = IDENTITY_MATRIX[index]
            idVal + (preset[index] - idVal) * intensity
        }

        val result = ColorMatrix(blended)

        val clampedSaturation = config.saturation.coerceIn(0f, 2f)
        if (abs(clampedSaturation - 1f) > 0.001f) {
            val satMatrix = ColorMatrix().apply {
                setSaturation(clampedSaturation)
            }
            result.postConcat(satMatrix)
        }

        val clampedContrast = config.contrast.coerceIn(0.5f, 1.6f)
        val clampedBrightness = config.brightness.coerceIn(-0.4f, 0.4f)
        if (abs(clampedContrast - 1f) > 0.001f || abs(clampedBrightness) > 0.001f) {
            val translate = (-0.5f * clampedContrast + 0.5f) * 255f + (clampedBrightness * 255f)
            val bcMatrix = ColorMatrix(
                floatArrayOf(
                    clampedContrast, 0f, 0f, 0f, translate,
                    0f, clampedContrast, 0f, 0f, translate,
                    0f, 0f, clampedContrast, 0f, translate,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            result.postConcat(bcMatrix)
        }

        return result.array.copyOf()
    }

    fun isIdentity(config: CameraFilterConfig): Boolean {
        val values = buildColorMatrixArray(config)
        for (i in 0 until 20) {
            if (abs(values[i] - IDENTITY_MATRIX[i]) > 0.002f) {
                return false
            }
        }
        return true
    }

    fun toAndroidColorMatrix(config: CameraFilterConfig): ColorMatrix {
        return ColorMatrix(buildColorMatrixArray(config))
    }

    fun toAndroidColorFilter(config: CameraFilterConfig): ColorMatrixColorFilter? {
        if (isIdentity(config)) return null
        return ColorMatrixColorFilter(toAndroidColorMatrix(config))
    }

    fun toComposeColorMatrix(config: CameraFilterConfig): androidx.compose.ui.graphics.ColorMatrix {
        return androidx.compose.ui.graphics.ColorMatrix(buildColorMatrixArray(config))
    }

    fun toComposeColorFilter(config: CameraFilterConfig): ColorFilter? {
        if (isIdentity(config)) return null
        return ColorFilter.colorMatrix(toComposeColorMatrix(config))
    }

    /**
     * Applies the ColorMatrix configuration in real time to a CameraX PreviewView
     * configured with ImplementationMode.COMPATIBLE (TextureView-backed).
     */
    fun applyToPreviewView(previewView: PreviewView, config: CameraFilterConfig) {
        val textureView = findTextureView(previewView) ?: return
        val androidColorFilter = toAndroidColorFilter(config)

        val paint = if (androidColorFilter != null) {
            Paint().apply {
                colorFilter = androidColorFilter
            }
        } else {
            null
        }
        textureView.setLayerPaint(paint)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (config.type == VideoFilterType.VIRTUAL_BLUR && config.intensity > 0.05f) {
                val radius = (18f * config.intensity.coerceIn(0f, 1f)).coerceAtLeast(1f)
                textureView.setRenderEffect(
                    RenderEffect.createBlurEffect(radius, radius, Shader.TileMode.CLAMP)
                )
            } else {
                textureView.setRenderEffect(null)
            }
        }
    }

    private fun findTextureView(view: View): TextureView? {
        if (view is TextureView) return view
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val found = findTextureView(view.getChildAt(i))
                if (found != null) return found
            }
        }
        return null
    }
}

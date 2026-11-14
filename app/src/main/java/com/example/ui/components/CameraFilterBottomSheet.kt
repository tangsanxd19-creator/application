package com.example.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CameraFilterConfig
import com.example.data.model.VideoFilterType
import com.example.ui.theme.CallDangerRed
import com.example.ui.theme.CallSuccessGreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.CyberVioletDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianCardElevated
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray
import kotlin.math.roundToInt

/**
 * Bottom sheet component for managing and previewing real-time camera filter effects
 * powered by standard CameraX 4x5 ColorMatrix configurations.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraFilterBottomSheet(
    initialConfig: CameraFilterConfig,
    isCameraOn: Boolean,
    isFrontCamera: Boolean,
    hasCameraPermission: Boolean,
    @DrawableRes fallbackPreviewRes: Int,
    onToggleFrontCamera: () -> Unit,
    onApplyFilter: (CameraFilterConfig) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var draftConfig by remember(initialConfig) { mutableStateOf(initialConfig) }
    var isComparingOriginal by remember { mutableStateOf(false) }
    var showMatrixInspector by remember { mutableStateOf(false) }
    var useSamplePreview by remember(isCameraOn, hasCameraPermission) {
        mutableStateOf(!isCameraOn || !hasCameraPermission)
    }

    val effectiveConfig = remember(draftConfig, isComparingOriginal) {
        if (isComparingOriginal) CameraFilterConfig(type = VideoFilterType.NONE) else draftConfig
    }

    val matrixValues = remember(effectiveConfig) {
        CameraColorMatrix.buildColorMatrixArray(effectiveConfig)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ObsidianCard,
        contentColor = PureWhite,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 12.dp, bottom = 4.dp)
                    .size(width = 44.dp, height = 4.dp)
                    .clip(CircleShape)
                    .background(ObsidianCardBorder)
            )
        },
        modifier = modifier.testTag("camera_filter_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .align(Alignment.CenterHorizontally)
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
            ) {
            // 1. Studio Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(listOf(CyberViolet, ElectricBlue))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = PureWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = stringResource(R.string.filter_studio_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.filter_studio_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Toggle 4x5 Matrix Inspector
                    IconButton(
                        onClick = { showMatrixInspector = !showMatrixInspector },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(if (showMatrixInspector) CyberVioletDark else ObsidianSurface)
                            .border(
                                1.dp,
                                if (showMatrixInspector) NeonCyan else ObsidianCardBorder,
                                CircleShape
                            )
                            .testTag("toggle_matrix_inspector_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = stringResource(R.string.filter_matrix_inspector_cd),
                            tint = if (showMatrixInspector) NeonCyan else SlateGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Reset Filter Button
                    IconButton(
                        onClick = {
                            draftConfig = CameraFilterConfig()
                            isComparingOriginal = false
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(CircleShape)
                            .background(ObsidianSurface)
                            .border(1.dp, ObsidianCardBorder, CircleShape)
                            .testTag("reset_filter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.filter_reset_cd),
                            tint = PureWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Real-Time Preview Stage
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(
                        width = 1.5.dp,
                        brush = Brush.linearGradient(listOf(CyberViolet, NeonCyan)),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .background(ObsidianDark)
                    .testTag("filter_live_preview")
            ) {
                val showLiveCameraX = !useSamplePreview && isCameraOn && hasCameraPermission

                if (showLiveCameraX) {
                    CameraPreviewView(
                        isCameraOn = true,
                        isFrontCamera = isFrontCamera,
                        hasCameraPermission = true,
                        filterConfig = effectiveConfig,
                        cornerRadius = 20.dp,
                        borderColor = Color.Transparent,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    val sampleColorFilter = remember(effectiveConfig) {
                        CameraColorMatrix.toComposeColorFilter(effectiveConfig)
                    }
                    val blurRadius = if (effectiveConfig.type == VideoFilterType.VIRTUAL_BLUR) {
                        (10f * effectiveConfig.intensity.coerceIn(0f, 1f)).dp
                    } else {
                        0.dp
                    }
                    Image(
                        painter = painterResource(id = fallbackPreviewRes),
                        contentDescription = stringResource(R.string.filter_preview_image_cd),
                        colorFilter = sampleColorFilter,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(if (blurRadius > 0.dp) Modifier.blur(blurRadius) else Modifier)
                    )
                    FilterOverlay(
                        filter = effectiveConfig.type,
                        intensity = effectiveConfig.intensity,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Subtle bottom readability gradient
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0x66080911),
                                    Color.Transparent,
                                    Color(0x88080911)
                                )
                            )
                        )
                )

                // Top-Start Active Filter Badge
                Surface(
                    color = Color(0xB3080911),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (isComparingOriginal) SlateGray else NeonCyan),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isComparingOriginal) SlateGray
                                    else if (showLiveCameraX) CallSuccessGreen
                                    else NeonCyan
                                )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val badgeLabel = if (isComparingOriginal) {
                            stringResource(R.string.filter_comparing_original)
                        } else if (draftConfig.type == VideoFilterType.NONE) {
                            draftConfig.type.titleAr
                        } else {
                            "${draftConfig.type.titleAr} • ${(draftConfig.intensity * 100).roundToInt()}%"
                        }
                        Text(
                            text = badgeLabel,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = PureWhite
                        )
                    }
                }

                // Top-End Camera Controls (Flip + Source Toggle)
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isCameraOn && hasCameraPermission) {
                        Surface(
                            color = Color(0xB3080911),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ObsidianCardBorder),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { useSamplePreview = !useSamplePreview }
                                .testTag("toggle_preview_source_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Videocam,
                                    contentDescription = null,
                                    tint = if (!useSamplePreview) CallSuccessGreen else SlateGray,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (!useSamplePreview) "كاميرا حية" else "معاينة صورة",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PureWhite
                                )
                            }
                        }

                        if (!useSamplePreview) {
                            IconButton(
                                onClick = onToggleFrontCamera,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xB3080911))
                                    .border(1.dp, ObsidianCardBorder, CircleShape)
                                    .testTag("sheet_flip_camera_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Cameraswitch,
                                    contentDescription = stringResource(R.string.switch_camera),
                                    tint = PureWhite,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom-End Compare Before / After Button
                Surface(
                    color = if (isComparingOriginal) CyberViolet else Color(0xCC0F121F),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (isComparingOriginal) PureWhite else CyberViolet),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(10.dp)
                        .minimumInteractiveComponentSize()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { isComparingOriginal = !isComparingOriginal }
                        .testTag("compare_original_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = stringResource(R.string.filter_compare_cd),
                            tint = PureWhite,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isComparingOriginal) "عرض الفلتر" else "قبل / بعد",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }
                }
            }

            // 3. Collapsible 4x5 ColorMatrix Inspector
            AnimatedVisibility(
                visible = showMatrixInspector,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ColorMatrixInspectorCard(
                    matrixValues = matrixValues,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp)
                        .testTag("matrix_inspector_card")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Filter Presets Carousel with Live ColorMatrix Thumbnails
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.filter_presets_section),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = PureWhite
                )
                Text(
                    text = draftConfig.type.titleEn,
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateGray
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 2.dp),
                modifier = Modifier.testTag("filter_presets_row")
            ) {
                items(VideoFilterType.entries, key = { it.id }) { preset ->
                    val isSelected = draftConfig.type == preset
                    FilterPresetThumbnailCard(
                        preset = preset,
                        isSelected = isSelected,
                        previewImageRes = fallbackPreviewRes,
                        onClick = {
                            isComparingOriginal = false
                            draftConfig = draftConfig.copy(
                                type = preset,
                                intensity = if (preset == VideoFilterType.NONE) 1.0f
                                else if (draftConfig.intensity < 0.1f) 1.0f
                                else draftConfig.intensity
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. ColorMatrix Parameter Sliders
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                border = BorderStroke(1.dp, ObsidianCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.filter_adjustments_section),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = PureWhite
                        )
                    }

                    // Intensity
                    FilterAdjustmentSlider(
                        label = stringResource(R.string.filter_param_intensity),
                        valueText = "${(draftConfig.intensity * 100).roundToInt()}%",
                        value = draftConfig.intensity,
                        valueRange = 0f..1f,
                        enabled = draftConfig.type != VideoFilterType.NONE,
                        accentColor = CyberViolet,
                        onValueChange = {
                            isComparingOriginal = false
                            draftConfig = draftConfig.copy(intensity = it)
                        },
                        testTag = "slider_intensity"
                    )

                    // Brightness
                    FilterAdjustmentSlider(
                        label = stringResource(R.string.filter_param_brightness),
                        valueText = "${if (draftConfig.brightness >= 0f) "+" else ""}${(draftConfig.brightness * 100).roundToInt()}%",
                        value = draftConfig.brightness,
                        valueRange = -0.35f..0.35f,
                        enabled = true,
                        accentColor = NeonCyan,
                        onValueChange = {
                            isComparingOriginal = false
                            draftConfig = draftConfig.copy(brightness = it)
                        },
                        testTag = "slider_brightness"
                    )

                    // Contrast
                    FilterAdjustmentSlider(
                        label = stringResource(R.string.filter_param_contrast),
                        valueText = "${(draftConfig.contrast * 100).roundToInt()}%",
                        value = draftConfig.contrast,
                        valueRange = 0.6f..1.6f,
                        enabled = true,
                        accentColor = ElectricBlue,
                        onValueChange = {
                            isComparingOriginal = false
                            draftConfig = draftConfig.copy(contrast = it)
                        },
                        testTag = "slider_contrast"
                    )

                    // Saturation
                    FilterAdjustmentSlider(
                        label = stringResource(R.string.filter_param_saturation),
                        valueText = "${(draftConfig.saturation * 100).roundToInt()}%",
                        value = draftConfig.saturation,
                        valueRange = 0f..2f,
                        enabled = true,
                        accentColor = CallSuccessGreen,
                        onValueChange = {
                            isComparingOriginal = false
                            draftConfig = draftConfig.copy(saturation = it)
                        },
                        testTag = "slider_saturation"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            }

            // 6. Pinned Footer Action Buttons
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("cancel_filter_button"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, ObsidianCardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SlateGray)
                ) {
                    Text(
                        text = stringResource(R.string.filter_action_cancel),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Button(
                    onClick = { onApplyFilter(draftConfig) },
                    modifier = Modifier
                        .weight(1.6f)
                        .height(50.dp)
                        .testTag("apply_filter_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberViolet)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.filter_action_apply),
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterPresetThumbnailCard(
    preset: VideoFilterType,
    isSelected: Boolean,
    @DrawableRes previewImageRes: Int,
    onClick: () -> Unit
) {
    val presetConfig = remember(preset) { CameraFilterConfig(type = preset, intensity = 1.0f) }
    val composeColorFilter = remember(presetConfig) {
        CameraColorMatrix.toComposeColorFilter(presetConfig)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(82.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(if (isSelected) ObsidianCardElevated else ObsidianSurface)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) NeonCyan else ObsidianCardBorder,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(6.dp)
            .testTag("filter_preset_${preset.id}")
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ObsidianDark)
        ) {
            Image(
                painter = painterResource(id = previewImageRes),
                contentDescription = preset.titleAr,
                colorFilter = composeColorFilter,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (preset == VideoFilterType.VIRTUAL_BLUR) Modifier.blur(4.dp) else Modifier
                    )
            )
            FilterOverlay(
                filter = preset,
                intensity = 0.85f,
                modifier = Modifier.fillMaxSize()
            )

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(CyberViolet),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = PureWhite,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = preset.titleAr,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            ),
            color = if (isSelected) PureWhite else SlateGray,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun FilterAdjustmentSlider(
    label: String,
    valueText: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    enabled: Boolean,
    accentColor: Color,
    onValueChange: (Float) -> Unit,
    testTag: String
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = if (enabled) PureWhite else SlateGray.copy(alpha = 0.5f)
            )
            Text(
                text = valueText,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (enabled) accentColor else SlateGray.copy(alpha = 0.5f)
            )
        }

        Slider(
            value = value.coerceIn(valueRange.start, valueRange.endInclusive),
            onValueChange = onValueChange,
            valueRange = valueRange,
            enabled = enabled,
            colors = SliderDefaults.colors(
                thumbColor = accentColor,
                activeTrackColor = accentColor,
                inactiveTrackColor = ObsidianCardBorder,
                disabledThumbColor = SlateGray,
                disabledActiveTrackColor = ObsidianCardBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
                .testTag(testTag)
        )
    }
}

@Composable
private fun ColorMatrixInspectorCard(
    matrixValues: FloatArray,
    modifier: Modifier = Modifier
) {
    val rowLabels = listOf("R", "G", "B", "A")
    val rowColors = listOf(CallDangerRed, CallSuccessGreen, ElectricBlue, SlateGray)

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianDark),
        border = BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = "CameraX ColorMatrix (4×5)",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                ),
                color = NeonCyan
            )

            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = rowLabels[row],
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        ),
                        color = rowColors[row],
                        modifier = Modifier.width(18.dp)
                    )
                    for (col in 0 until 5) {
                        val value = matrixValues[row * 5 + col]
                        val formatted = if (col == 4) {
                            String.format("%+5.1f", value)
                        } else {
                            String.format("%5.2f", value)
                        }
                        Text(
                            text = formatted,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            color = PureWhite,
                            textAlign = TextAlign.End,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

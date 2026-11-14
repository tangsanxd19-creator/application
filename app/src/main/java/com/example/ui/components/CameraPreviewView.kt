package com.example.ui.components

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.CameraFilterConfig
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.SlateGray

@Composable
fun CameraPreviewView(
    isCameraOn: Boolean,
    isFrontCamera: Boolean,
    hasCameraPermission: Boolean,
    modifier: Modifier = Modifier,
    filterConfig: CameraFilterConfig = CameraFilterConfig(),
    cornerRadius: Dp = 16.dp,
    borderColor: Color = ElectricBlue
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestFilterConfig by rememberUpdatedState(filterConfig)

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
            setOnHierarchyChangeListener(object : ViewGroup.OnHierarchyChangeListener {
                override fun onChildViewAdded(parent: View?, child: View?) {
                    post {
                        CameraColorMatrix.applyToPreviewView(this@apply, latestFilterConfig)
                    }
                }

                override fun onChildViewRemoved(parent: View?, child: View?) = Unit
            })
        }
    }

    LaunchedEffect(filterConfig, isCameraOn, hasCameraPermission) {
        if (isCameraOn && hasCameraPermission) {
            CameraColorMatrix.applyToPreviewView(previewView, filterConfig)
        }
    }

    DisposableEffect(isCameraOn, isFrontCamera, hasCameraPermission) {
        if (!isCameraOn || !hasCameraPermission) {
            return@DisposableEffect onDispose {}
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val executor = ContextCompat.getMainExecutor(context)

        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()

                val cameraSelector = if (isFrontCamera) {
                    CameraSelector.DEFAULT_FRONT_CAMERA
                } else {
                    CameraSelector.DEFAULT_BACK_CAMERA
                }

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview
                )

                previewView.post {
                    CameraColorMatrix.applyToPreviewView(previewView, latestFilterConfig)
                }
            } catch (e: Exception) {
                Log.e("CameraPreviewView", "Camera binding failed", e)
            }
        }, executor)

        onDispose {
            try {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll()
            } catch (e: Exception) {
                Log.w("CameraPreviewView", "Unbinding camera on dispose", e)
            }
        }
    }

    val shape = RoundedCornerShape(cornerRadius)

    Box(
        modifier = modifier
            .clip(shape)
            .border(1.5.dp, borderColor, shape)
            .background(ObsidianCard),
        contentAlignment = Alignment.Center
    ) {
        if (isCameraOn && hasCameraPermission) {
            AndroidView(
                factory = { previewView },
                update = { view ->
                    CameraColorMatrix.applyToPreviewView(view, filterConfig)
                },
                modifier = Modifier.fillMaxSize()
            )
            FilterOverlay(
                filter = filterConfig.type,
                intensity = filterConfig.intensity,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF131625)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.VideocamOff,
                    contentDescription = "الكاميرا متوقفة",
                    tint = SlateGray
                )
            }
        }
    }
}

package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PersonaRepository
import com.example.data.model.Persona
import com.example.ui.theme.CallSuccessGreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SmartMatchScreen(
    repository: PersonaRepository,
    onBackClick: () -> Unit,
    onStartCall: (String) -> Unit
) {
    BackHandler { onBackClick() }

    val scope = rememberCoroutineScope()
    val personas by repository.personas.collectAsState()

    val availableInterests = listOf(
        "الذكاء الاصطناعي",
        "تعلم اللغات",
        "البرمجة",
        "تطوير الذات",
        "السفر والثقافة",
        "الفن والموسيقى",
        "ريادة الأعمال",
        "التفكير الإيجابي"
    )

    val selectedInterests = remember { mutableStateListOf("الذكاء الاصطناعي", "تعلم اللغات") }
    var isScanning by remember { mutableStateOf(false) }
    var matchedPersona by remember { mutableStateOf<Persona?>(null) }
    var matchPercentage by remember { mutableStateOf(96) }

    val infiniteTransition = rememberInfiniteTransition(label = "radar_anim")
    val radarAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_sweep"
    )
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_radius"
    )

    fun startScanning() {
        if (isScanning) return
        scope.launch {
            isScanning = true
            matchedPersona = null
            delay(2400L)
            // Match based on selected interests or best candidate
            val best = personas.maxByOrNull { persona ->
                persona.interests.count { it in selectedInterests }
            } ?: personas.first()
            matchedPersona = best
            matchPercentage = 88 + (selectedInterests.size * 3).coerceAtMost(11)
            isScanning = false
        }
    }

    Scaffold(
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "المطابقة الذكية بالاهتمامات",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "رجوع",
                            tint = PureWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0F121F))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("smart_match_screen"),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "اختر اهتماماتك ليقوم رادار الذكاء الاصطناعي بربطك بالشخصية أو الشريك الأنسب لمحادثة فيديو ملهمة.",
                style = MaterialTheme.typography.bodyMedium,
                color = SlateGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Interest selection chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                availableInterests.forEach { interest ->
                    val isSelected = interest in selectedInterests
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            if (isSelected) {
                                if (selectedInterests.size > 1) selectedInterests.remove(interest)
                            } else {
                                selectedInterests.add(interest)
                            }
                        },
                        label = { Text(interest) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = ObsidianCard,
                            labelColor = SlateGray,
                            selectedContainerColor = CyberViolet,
                            selectedLabelColor = PureWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = ObsidianCardBorder,
                            selectedBorderColor = NeonCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Radar Scanner Visual Box
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0D101C))
                    .border(2.dp, ObsidianCardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val maxRadius = size.minDimension / 2f

                    // Concentric rings
                    listOf(0.25f, 0.5f, 0.75f, 1f).forEach { fraction ->
                        drawCircle(
                            color = Color(0x338B5CF6),
                            radius = maxRadius * fraction,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )
                    }

                    // Crosshair grid lines
                    drawLine(
                        color = Color(0x228B5CF6),
                        start = Offset(0f, center.y),
                        end = Offset(size.width, center.y),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = Color(0x228B5CF6),
                        start = Offset(center.x, 0f),
                        end = Offset(center.x, size.height),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Expanding radar pulse wave
                    if (isScanning) {
                        drawCircle(
                            color = Color(0x4406B6D4),
                            radius = maxRadius * radarPulse,
                            center = center,
                            style = Stroke(width = 2.dp.toPx())
                        )

                        // Rotating radar sweep line
                        val rad = Math.toRadians(radarAngle.toDouble())
                        val endX = center.x + maxRadius * cos(rad).toFloat()
                        val endY = center.y + maxRadius * sin(rad).toFloat()
                        drawLine(
                            brush = Brush.linearGradient(
                                colors = listOf(NeonCyan, Color.Transparent),
                                start = center,
                                end = Offset(endX, endY)
                            ),
                            start = center,
                            end = Offset(endX, endY),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                }

                // Center radar icon
                Icon(
                    imageVector = Icons.Default.Radar,
                    contentDescription = "رادار",
                    tint = if (isScanning) NeonCyan else CyberViolet,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button: Start Scan
            Button(
                onClick = { startScanning() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("scan_match_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberViolet),
                enabled = !isScanning
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = PureWhite,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("جاري مسح الشبكة الذكية...", color = PureWhite)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "بحث",
                            tint = PureWhite
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "بدء مسح الرادار الذكي",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                    }
                }
            }

            // Matched Persona Card
            matchedPersona?.let { match ->
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("match_result_card"),
                    colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                    shape = RoundedCornerShape(22.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, NeonCyan)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x3310B981))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "✨ نسبة توافق الاهتمامات: $matchPercentage%",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = CallSuccessGreen
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Image(
                            painter = painterResource(id = match.avatarRes),
                            contentDescription = match.nameAr,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .border(2.dp, CyberViolet, RoundedCornerShape(22.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = match.nameAr,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = PureWhite
                        )
                        Text(
                            text = match.roleAr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NeonCyan
                        )

                        Text(
                            text = match.bioAr,
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateGray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )

                        Button(
                            onClick = { onStartCall(match.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("match_start_call_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CallSuccessGreen)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "بدء مكالمة مع الشريك",
                                tint = PureWhite
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بدء مكالمة فيديو مع ${match.nameAr}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PureWhite
                            )
                        }
                    }
                }
            }
        }
    }
}

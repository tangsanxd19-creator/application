package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.PersonaRepository
import com.example.data.model.AppLanguage
import com.example.data.model.CallHistoryItem
import com.example.data.model.CameraFilterConfig
import com.example.data.model.ChatMessage
import com.example.data.model.Persona
import com.example.data.model.VideoFilterType
import com.example.service.AiChatService
import com.example.service.SpeechManager
import com.example.ui.components.AudioWaveVisualizer
import com.example.ui.components.CameraColorMatrix
import com.example.ui.components.CameraFilterBottomSheet
import com.example.ui.components.CameraPreviewView
import com.example.ui.components.FilterOverlay
import com.example.ui.theme.CallDangerRed
import com.example.ui.theme.CallSuccessGreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCallScreen(
    personaId: String,
    repository: PersonaRepository,
    aiService: AiChatService,
    speechManager: SpeechManager,
    onEndCall: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val persona = remember(personaId) {
        repository.getPersonaById(personaId) ?: repository.personas.value.first()
    }

    var isMicMuted by remember { mutableStateOf(false) }
    var isCameraOn by remember { mutableStateOf(true) }
    var isFrontCamera by remember { mutableStateOf(true) }
    var activeFilterConfig by remember {
        mutableStateOf(CameraFilterConfig(type = VideoFilterType.CYBER_GLOW))
    }
    var showFilterSheet by remember { mutableStateOf(false) }
    var showChatDrawer by remember { mutableStateOf(false) }
    var callSeconds by remember { mutableIntStateOf(0) }

    var targetLanguage by remember { mutableStateOf(AppLanguage.ARABIC) }
    var liveSubtitleOriginal by remember { mutableStateOf("") }
    var liveSubtitleTranslated by remember { mutableStateOf("") }
    var inCallMessageInput by remember { mutableStateOf("") }

    val isSpeaking by speechManager.isSpeaking.collectAsState()
    val isListening by speechManager.isListening.collectAsState()

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        hasCameraPermission = perms[Manifest.permission.CAMERA] ?: false
        hasAudioPermission = perms[Manifest.permission.RECORD_AUDIO] ?: false
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission || !hasAudioPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
            )
        }
    }

    // Call duration timer
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000L)
            callSeconds++
        }
    }

    // Initial greeting from AI persona when call connects
    LaunchedEffect(persona) {
        delay(1200L)
        val initialPrompt = "أهلاً بك! أنا ${persona.nameAr}. كيف أستطيع مساعدتك في هذه المكالمة؟"
        liveSubtitleOriginal = initialPrompt
        val translated = aiService.translateText(initialPrompt, persona.defaultLanguage, AppLanguage.ENGLISH)
        liveSubtitleTranslated = translated
        speechManager.speak(initialPrompt, persona.defaultLanguage, persona.voicePitch, persona.voiceRate)
    }

    // Clean up call when leaving
    DisposableEffect(Unit) {
        onDispose {
            speechManager.stopSpeaking()
            speechManager.stopListening()
            repository.addCallRecord(
                CallHistoryItem(
                    personaId = persona.id,
                    personaName = persona.nameAr,
                    avatarRes = persona.avatarRes,
                    durationSeconds = callSeconds,
                    isVideo = true
                )
            )
        }
    }

    val personaFlow = remember(persona.id) { repository.getMessagesFlow(persona.id) }
    val inCallMessages by personaFlow.collectAsState()

    // Breathing / talking animation for AI character video
    val infiniteTransition = rememberInfiniteTransition(label = "persona_anim")
    val avatarScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isSpeaking) 1.05f else 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isSpeaking) 600 else 2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "avatar_scale"
    )

    fun sendUserSpeechOrText(input: String) {
        if (input.isBlank()) return
        scope.launch {
            val userMsg = ChatMessage(
                senderName = "أنت",
                isUser = true,
                originalText = input,
                originalLanguage = targetLanguage
            )
            repository.addMessage(persona.id, userMsg)
            liveSubtitleOriginal = input

            val history = inCallMessages.map { (if (it.isUser) "User" else "Assistant") to it.originalText }
            val response = aiService.generatePersonaResponse(persona, history, input, targetLanguage)
            liveSubtitleOriginal = response
            val translated = aiService.translateText(response, targetLanguage, if (targetLanguage == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC)
            liveSubtitleTranslated = translated

            val aiMsg = ChatMessage(
                senderName = persona.nameAr,
                isUser = false,
                originalText = response,
                translatedText = translated,
                originalLanguage = targetLanguage
            )
            repository.addMessage(persona.id, aiMsg)

            if (!isMicMuted) {
                speechManager.speak(response, targetLanguage, persona.voicePitch, persona.voiceRate)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ObsidianDark)
            .testTag("video_call_screen")
    ) {
        // Main Video: AI Character Persona Feed
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val feedColorFilter = remember(activeFilterConfig) {
                CameraColorMatrix.toComposeColorFilter(activeFilterConfig)
            }
            Image(
                painter = painterResource(id = persona.avatarRes),
                contentDescription = persona.nameAr,
                colorFilter = feedColorFilter,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(avatarScale),
                contentScale = ContentScale.Crop
            )

            // Video Filter Overlay
            FilterOverlay(
                filter = activeFilterConfig.type,
                intensity = activeFilterConfig.intensity
            )

            // Cyber Vignette & Gradient Overlays
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xB3080911),
                                Color.Transparent,
                                Color(0x99080911),
                                Color(0xF2080911)
                            )
                        )
                    )
            )
        }

        // Top Status Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 40.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CallSuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = persona.nameAr,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = PureWhite
                    )
                }
                Text(
                    text = "${String.format("%02d", callSeconds / 60)}:${String.format("%02d", callSeconds % 60)} • HD مباشر",
                    style = MaterialTheme.typography.bodyMedium,
                    color = NeonCyan
                )
            }

            // Language Selector Toggle Chip
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x66121422))
                    .border(1.dp, CyberViolet, RoundedCornerShape(16.dp))
                    .clickable {
                        targetLanguage = when (targetLanguage) {
                            AppLanguage.ARABIC -> AppLanguage.ENGLISH
                            AppLanguage.ENGLISH -> AppLanguage.FRENCH
                            AppLanguage.FRENCH -> AppLanguage.ARABIC
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = "تغيير لغة الترجمة",
                        tint = NeonCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = targetLanguage.displayNameAr,
                        style = MaterialTheme.typography.labelMedium,
                        color = PureWhite
                    )
                }
            }
        }

        // User PiP Camera Feed (Top Right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 96.dp, end = 20.dp)
                .size(width = 110.dp, height = 155.dp)
                .testTag("user_camera_pip")
        ) {
            CameraPreviewView(
                isCameraOn = isCameraOn && !showFilterSheet,
                isFrontCamera = isFrontCamera,
                hasCameraPermission = hasCameraPermission,
                filterConfig = activeFilterConfig,
                modifier = Modifier.fillMaxSize()
            )

            // Switch Camera Mini Button inside PiP
            if (isCameraOn) {
                IconButton(
                    onClick = { isFrontCamera = !isFrontCamera },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0x88000000))
                ) {
                    Icon(
                        imageVector = Icons.Default.Cameraswitch,
                        contentDescription = "تبديل الكاميرا",
                        tint = PureWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Live Audio Wave Visualizer & Subtitles Ribbon (Bottom Center Above Controls)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Audio wave pulses when speaking
            AudioWaveVisualizer(
                isSpeaking = isSpeaking || isListening,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Live Translation & Subtitles Card
            if (liveSubtitleOriginal.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("live_subtitle_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xD90E111F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x408B5CF6))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isSpeaking) persona.nameAr else "الكلام المباشر",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "ترجمة ذكية فورية",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateGray
                            )
                        }

                        Text(
                            text = liveSubtitleOriginal,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = PureWhite,
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        if (liveSubtitleTranslated.isNotBlank()) {
                            Text(
                                text = liveSubtitleTranslated,
                                style = MaterialTheme.typography.bodySmall,
                                color = ElectricBlue,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // Call Interactive Control Bar (Bottom Floating Dock)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xF2121422)),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0x408B5CF6))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mic Mute / Unmute
                    IconButton(
                        onClick = {
                            isMicMuted = !isMicMuted
                            if (isMicMuted) speechManager.stopSpeaking()
                        },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (isMicMuted) Color(0x33EF4444) else Color(0x22FFFFFF))
                            .testTag("mute_button")
                    ) {
                        Icon(
                            imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMicMuted) "إلغاء كتم الصوت" else "كتم الصوت",
                            tint = if (isMicMuted) CallDangerRed else PureWhite
                        )
                    }

                    // Video Camera Toggle
                    IconButton(
                        onClick = { isCameraOn = !isCameraOn },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(if (!isCameraOn) Color(0x33EF4444) else Color(0x22FFFFFF))
                            .testTag("camera_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = if (isCameraOn) "إيقاف الكاميرا" else "تشغيل الكاميرا",
                            tint = if (!isCameraOn) CallDangerRed else PureWhite
                        )
                    }

                    // Speech Recognition Input Button (Push to talk / Listen)
                    IconButton(
                        onClick = {
                            if (isListening) {
                                speechManager.stopListening()
                            } else {
                                speechManager.startListening(targetLanguage) { recognizedQuery ->
                                    sendUserSpeechOrText(recognizedQuery)
                                }
                            }
                        },
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(if (isListening) NeonCyan else CyberViolet)
                            .testTag("voice_listen_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "التحدث بالصوت",
                            tint = PureWhite,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Smart Filters Studio
                    IconButton(
                        onClick = { showFilterSheet = true },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("filter_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "الفلاتر الذكية",
                            tint = NeonCyan
                        )
                    }

                    // In-Call Chat Drawer Toggle
                    IconButton(
                        onClick = { showChatDrawer = true },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0x22FFFFFF))
                            .testTag("in_call_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "الدردشة النصية",
                            tint = PureWhite
                        )
                    }

                    // End Call Button
                    IconButton(
                        onClick = onEndCall,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(CallDangerRed)
                            .testTag("end_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "إنهاء المكالمة",
                            tint = PureWhite,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }

    // Smart Filters Bottom Sheet
    if (showFilterSheet) {
        CameraFilterBottomSheet(
            initialConfig = activeFilterConfig,
            isCameraOn = isCameraOn,
            isFrontCamera = isFrontCamera,
            hasCameraPermission = hasCameraPermission,
            fallbackPreviewRes = persona.avatarRes,
            onToggleFrontCamera = { isFrontCamera = !isFrontCamera },
            onApplyFilter = { updatedConfig ->
                activeFilterConfig = updatedConfig
                showFilterSheet = false
            },
            onDismiss = { showFilterSheet = false }
        )
    }

    // In-Call Chat Drawer Bottom Sheet
    if (showChatDrawer) {
        ModalBottomSheet(
            onDismissRequest = { showChatDrawer = false },
            sheetState = rememberModalBottomSheetState(),
            containerColor = ObsidianDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                Text(
                    text = "دردشة أثناء المكالمة مع ${persona.nameAr}",
                    style = MaterialTheme.typography.titleMedium,
                    color = PureWhite
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Input Bar inside drawer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inCallMessageInput,
                        onValueChange = { inCallMessageInput = it },
                        placeholder = { Text("اكتب رسالة سريعة...", color = SlateGray) },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("in_call_message_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyberViolet,
                            unfocusedBorderColor = Color(0xFF262C4A),
                            focusedTextColor = PureWhite,
                            unfocusedTextColor = PureWhite
                        ),
                        shape = RoundedCornerShape(14.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(
                        onClick = {
                            if (inCallMessageInput.isNotBlank()) {
                                val text = inCallMessageInput
                                inCallMessageInput = ""
                                sendUserSpeechOrText(text)
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(CyberViolet)
                            .testTag("in_call_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "إرسال",
                            tint = PureWhite
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

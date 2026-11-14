package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PersonaRepository
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.example.service.AiChatService
import com.example.service.SpeechManager
import com.example.ui.theme.CallSuccessGreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    personaId: String,
    repository: PersonaRepository,
    aiService: AiChatService,
    speechManager: SpeechManager,
    onBackClick: () -> Unit,
    onStartCall: (String) -> Unit
) {
    BackHandler { onBackClick() }

    val scope = rememberCoroutineScope()
    val persona = remember(personaId) {
        repository.getPersonaById(personaId) ?: repository.personas.value.first()
    }

    val messageFlow = remember(persona.id) { repository.getMessagesFlow(persona.id) }
    val messages by messageFlow.collectAsState()

    var textInput by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var targetLanguage by remember { mutableStateOf(AppLanguage.ARABIC) }

    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun handleSend(text: String) {
        if (text.isBlank() || isGenerating) return
        val userQuery = text.trim()
        textInput = ""

        val userMessage = ChatMessage(
            senderName = "أنت",
            isUser = true,
            originalText = userQuery,
            originalLanguage = targetLanguage
        )
        repository.addMessage(persona.id, userMessage)

        scope.launch {
            isGenerating = true
            val history = messages.map { (if (it.isUser) "User" else "Assistant") to it.originalText }
            val reply = aiService.generatePersonaResponse(persona, history, userQuery, targetLanguage)
            val translated = aiService.translateText(reply, targetLanguage, if (targetLanguage == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC)

            val aiMessage = ChatMessage(
                senderName = persona.nameAr,
                isUser = false,
                originalText = reply,
                translatedText = translated,
                originalLanguage = targetLanguage
            )
            repository.addMessage(persona.id, aiMessage)
            isGenerating = false
        }
    }

    Scaffold(
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = persona.avatarRes),
                            contentDescription = persona.nameAr,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.dp, CyberViolet, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = persona.nameAr,
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CallSuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "متصل بالذكاء الاصطناعي",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonCyan
                                )
                            }
                        }
                    }
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
                actions = {
                    IconButton(
                        onClick = { onStartCall(persona.id) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(CircleShape)
                            .background(Color(0x338B5CF6))
                            .testTag("chat_to_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "بدء مكالمة فيديو",
                            tint = CyberViolet
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
        ) {
            // Suggestion Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val suggestions = when (persona.id) {
                    "layla" -> listOf(
                        "كيف أقول 'يسعدني لقاؤك' بالفرنسية؟",
                        "علمني جمل شائعة في المطار بالإنجليزي",
                        "لنمارس حواراً بسيطاً عن السفر"
                    )
                    "alex" -> listOf(
                        "ما هو أفضل هيكل معماري لتطبيق أندرويد؟",
                        "كيف أدمج الذكاء الاصطناعي مع الكاميرا؟",
                        "اشرح لي مفهوم Clean Architecture"
                    )
                    "nova" -> listOf(
                        "لخص لي أهم ميزات الذكاء الاصطناعي في الاتصال",
                        "ساعدني في جدول مهامي اليومية",
                        "اقترح أفكاراً مبتكرة لمشروع تقني"
                    )
                    else -> listOf(
                        "أشعر بالتوتر، بماذا تنصحني؟",
                        "كيف أحافظ على طاقتي الإيجابية؟",
                        "شاركني اقتباساً ملهماً لليوم"
                    )
                }

                items(suggestions) { prompt ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(ObsidianCard)
                            .border(1.dp, ObsidianCardBorder, RoundedCornerShape(14.dp))
                            .clickable { handleSend(prompt) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = prompt,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGray
                        )
                    }
                }
            }

            // Message List
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    MessageBubble(
                        message = message,
                        onSpeak = {
                            speechManager.speak(
                                message.originalText,
                                message.originalLanguage,
                                persona.voicePitch,
                                persona.voiceRate
                            )
                        },
                        onTranslate = {
                            scope.launch {
                                val target = if (message.originalLanguage == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
                                val trans = aiService.translateText(message.originalText, message.originalLanguage, target)
                                val updated = message.copy(translatedText = trans)
                                repository.addMessage(persona.id, updated)
                            }
                        }
                    )
                }

                if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = CyberViolet,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${persona.nameAr} تكتب الآن...",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateGray
                            )
                        }
                    }
                }
            }

            // Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("اكتب رسالتك...", color = SlateGray) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberViolet,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite,
                        focusedContainerColor = ObsidianCard,
                        unfocusedContainerColor = ObsidianCard
                    ),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Voice Dictation Button
                IconButton(
                    onClick = {
                        speechManager.startListening(targetLanguage) { recognized ->
                            handleSend(recognized)
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0x3306B6D4))
                        .testTag("chat_voice_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "إدخال صوتي",
                        tint = NeonCyan
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Send Button
                IconButton(
                    onClick = { handleSend(textInput) },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CyberViolet)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "إرسال",
                        tint = PureWhite
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onSpeak: () -> Unit,
    onTranslate: () -> Unit
) {
    val isUser = message.isUser
    val alignment = if (isUser) Alignment.End else Alignment.Start

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 310.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isUser) 18.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 18.dp
                    )
                )
                .background(if (isUser) CyberViolet else ObsidianCard)
                .border(
                    1.dp,
                    if (isUser) Color.Transparent else ObsidianCardBorder,
                    RoundedCornerShape(18.dp)
                )
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = message.originalText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PureWhite
                )

                if (!message.translatedText.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x2206B6D4))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = message.translatedText,
                            style = MaterialTheme.typography.bodySmall,
                            color = NeonCyan
                        )
                    }
                }

                // Bubble Action Tools for AI Messages (TTS Playback & Translation)
                if (!isUser) {
                    Row(
                        modifier = Modifier
                            .padding(top = 6.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onTranslate,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Translate,
                                contentDescription = "ترجمة الرسالة",
                                tint = SlateGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        IconButton(
                            onClick = onSpeak,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "تشغيل الصوت",
                                tint = SlateGray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

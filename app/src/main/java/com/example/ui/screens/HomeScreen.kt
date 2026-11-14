package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.PersonaRepository
import com.example.data.model.Persona
import com.example.ui.theme.CallSuccessGreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.CyberVioletDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: PersonaRepository,
    onStartCall: (String) -> Unit,
    onOpenChat: (String) -> Unit,
    onNavigateToMatch: () -> Unit,
    onNavigateToGallery: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val personas by repository.personas.collectAsState()
    val callHistory by repository.callHistory.collectAsState()

    var selectedCategory by remember { mutableStateOf("all") }
    var showCreatePersonaDialog by remember { mutableStateOf(false) }

    val filteredPersonas = remember(personas, selectedCategory) {
        if (selectedCategory == "all") personas else personas.filter { it.category == selectedCategory }
    }

    Scaffold(
        containerColor = ObsidianDark,
        bottomBar = {
            NavigationBar(
                containerColor = ObsidianSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Videocam, contentDescription = "الرئيسية") },
                    label = { Text("المكالمات") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PureWhite,
                        selectedTextColor = CyberViolet,
                        indicatorColor = CyberVioletDark
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToGallery,
                    icon = { Icon(Icons.Default.SmartToy, contentDescription = "معرض الشخصيات") },
                    label = { Text("المعرض") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToMatch,
                    icon = { Icon(Icons.Default.Radar, contentDescription = "مطابقة ذكية") },
                    label = { Text("مطابقة") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToSettings,
                    icon = { Icon(Icons.Default.Settings, contentDescription = "الإعدادات") },
                    label = { Text("الإعدادات") },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = SlateGray,
                        unselectedTextColor = SlateGray
                    )
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("home_screen_content"),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero Banner with Animated Hologram Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_hero),
                        contentDescription = "AI CONNECT Hero Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x33000000),
                                        Color(0x99080911),
                                        ObsidianDark
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text(
                                    text = "AI CONNECT",
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp
                                    ),
                                    color = PureWhite
                                )
                                Text(
                                    text = "تواصل بلا حدود • محادثات فيديو ذكية",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = NeonCyan
                                )
                            }

                            // Glowing quick status pill
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0x3310B981))
                                    .border(1.dp, CallSuccessGreen, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(CallSuccessGreen)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "متصل بالذكاء الاصطناعي",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CallSuccessGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Big Action Button: Instant Video Call
            item {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse_button")
                val pulseScale by infiniteTransition.animateFloat(
                    initialValue = 0.99f,
                    targetValue = 1.02f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulse"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Button(
                        onClick = {
                            val target = personas.firstOrNull() ?: return@Button
                            onStartCall(target.id)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .scale(pulseScale)
                            .testTag("instant_call_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberViolet
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Videocam,
                                contentDescription = "اتصال فوري",
                                modifier = Modifier.size(26.dp),
                                tint = PureWhite
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "بدء مكالمة فيديو ذكية فورية",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PureWhite
                            )
                        }
                    }
                }
            }

            // Categories Filter Chips & Add Persona Button
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "الشخصيات الافتراضية الذكية",
                        style = MaterialTheme.typography.titleLarge,
                        color = PureWhite
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TextButton(
                            onClick = onNavigateToGallery,
                            modifier = Modifier.testTag("open_gallery_button")
                        ) {
                            Text("المعرض", color = NeonCyan, style = MaterialTheme.typography.labelMedium)
                        }

                        IconButton(
                            onClick = { showCreatePersonaDialog = true },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(ObsidianCard)
                                .border(1.dp, CyberViolet, CircleShape)
                                .testTag("add_persona_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "إنشاء شخصية جديدة",
                                tint = CyberViolet
                            )
                        }
                    }
                }

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val categories = listOf(
                        "all" to "الكل",
                        "assistant" to "مساعد ذكي",
                        "tutor" to "معلم لغات",
                        "mentor" to "مرشد تقني",
                        "friend" to "أصدقاء"
                    )

                    items(categories) { (key, label) ->
                        val isSelected = selectedCategory == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = key },
                            label = { Text(label) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = ObsidianCard,
                                labelColor = SlateGray,
                                selectedContainerColor = ElectricBlueDark,
                                selectedLabelColor = PureWhite
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = ObsidianCardBorder,
                                selectedBorderColor = ElectricBlue
                            )
                        )
                    }
                }
            }

            // Personas Cards List
            items(filteredPersonas, key = { it.id }) { persona ->
                PersonaCard(
                    persona = persona,
                    onCallClick = { onStartCall(persona.id) },
                    onChatClick = { onOpenChat(persona.id) },
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
                )
            }

            // Recent Calls Section
            if (callHistory.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "سجل المكالمات",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "سجل المكالمات الأخيرة",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite
                        )
                    }
                }

                items(callHistory.take(3), key = { it.id }) { historyItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 4.dp)
                            .clickable { onStartCall(historyItem.personaId) },
                        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = historyItem.avatarRes),
                                contentDescription = historyItem.personaName,
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = historyItem.personaName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PureWhite
                                )
                                Text(
                                    text = "المدة: ${historyItem.durationSeconds / 60} دقيقة و ${historyItem.durationSeconds % 60} ثانية",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = SlateGray
                                )
                            }
                            IconButton(onClick = { onStartCall(historyItem.personaId) }) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "إعادة الاتصال",
                                    tint = CallSuccessGreen
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreatePersonaDialog) {
        CreatePersonaDialog(
            onDismiss = { showCreatePersonaDialog = false },
            onCreate = { name, role, bio, category, interests, prompt ->
                repository.createCustomPersona(name, role, bio, category, interests, prompt)
                showCreatePersonaDialog = false
            }
        )
    }
}

@Composable
fun PersonaCard(
    persona: Persona,
    onCallClick: () -> Unit,
    onChatClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("persona_card_${persona.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with online green ring
                Box(contentAlignment = Alignment.BottomEnd) {
                    Image(
                        painter = painterResource(id = persona.avatarRes),
                        contentDescription = persona.nameAr,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.5.dp, CyberViolet, RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(CallSuccessGreen)
                            .border(2.dp, ObsidianDark, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = persona.nameAr,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PureWhite
                    )
                    Text(
                        text = persona.roleAr,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonCyan
                    )

                    // Interests tags row
                    Row(
                        modifier = Modifier.padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        persona.interests.take(2).forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x228B5CF6))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = PureWhite
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = persona.bioAr,
                style = MaterialTheme.typography.bodyMedium,
                color = SlateGray,
                modifier = Modifier.padding(top = 10.dp, bottom = 12.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Buttons: Video Call and Text Chat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onCallClick,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(44.dp)
                        .testTag("call_btn_${persona.id}"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberViolet)
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "مكالمة فيديو",
                        modifier = Modifier.size(18.dp),
                        tint = PureWhite
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "مكالمة فيديو", color = PureWhite, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onChatClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("chat_btn_${persona.id}"),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ElectricBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "دردشة",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "دردشة", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun CreatePersonaDialog(
    onDismiss: () -> Unit,
    onCreate: (name: String, role: String, bio: String, category: String, interests: List<String>, prompt: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var prompt by remember { mutableStateOf("") }
    var interestsText by remember { mutableStateOf("الذكاء الاصطناعي, التحدث") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إنشاء شخصية افتراضية ذكية",
                style = MaterialTheme.typography.titleLarge,
                color = PureWhite
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم الشخصية") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberViolet,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("الدور (مثال: مستشار، مترجم)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberViolet,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("نبذة مختصرة") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberViolet,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("تعليمات الذكاء الاصطناعي (System Prompt)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyberViolet,
                        unfocusedBorderColor = ObsidianCardBorder,
                        focusedTextColor = PureWhite,
                        unfocusedTextColor = PureWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val tags = interestsText.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        onCreate(name, role.ifBlank { "مساعد ذكي" }, bio.ifBlank { "شخصية ذكية جديدة" }, "assistant", tags, prompt)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CyberViolet)
            ) {
                Text("حفظ وإنشاء", color = PureWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء", color = SlateGray)
            }
        },
        containerColor = ObsidianCard
    )
}

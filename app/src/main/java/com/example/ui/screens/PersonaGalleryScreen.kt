package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PersonaRepository
import com.example.data.model.Persona
import com.example.ui.theme.CallSuccessGreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.CyberVioletDark
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.ObsidianSurface
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonaGalleryScreen(
    repository: PersonaRepository,
    onBackClick: () -> Unit,
    onStartCall: (String) -> Unit,
    onOpenChat: (String) -> Unit
) {
    BackHandler { onBackClick() }

    val personas by repository.personas.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }

    val filteredPersonas = remember(personas, searchQuery, selectedCategory) {
        personas.filter { persona ->
            val matchesCategory = selectedCategory == "all" || persona.category == selectedCategory

            val matchesSearch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.trim().lowercase()

                val categoryMatches = when {
                    "مساعد" in q || "assistant" in q -> persona.category == "assistant"
                    "لغ" in q || "معلم" in q || "tutor" in q || "language" in q -> persona.category == "tutor"
                    "تقن" in q || "برمج" in q || "mentor" in q || "tech" in q -> persona.category == "mentor"
                    "صديق" in q || "friend" in q || "اجتماع" in q -> persona.category == "friend"
                    else -> persona.category.contains(q, ignoreCase = true)
                }

                persona.nameAr.contains(q, ignoreCase = true) ||
                        persona.nameEn.contains(q, ignoreCase = true) ||
                        persona.roleAr.contains(q, ignoreCase = true) ||
                        persona.roleEn.contains(q, ignoreCase = true) ||
                        persona.bioAr.contains(q, ignoreCase = true) ||
                        persona.interests.any { it.contains(q, ignoreCase = true) } ||
                        categoryMatches
            }

            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        containerColor = ObsidianDark,
        topBar = {
            // Persistent Top Bar with Search & Category Filters
            Surface(
                color = ObsidianSurface,
                tonalElevation = 6.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 8.dp)
                        .testTag("persistent_search_bar_header")
                ) {
                    // Persistent Search Bar Row with Back Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0x22FFFFFF))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "رجوع",
                                tint = PureWhite
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Persistent Search Input
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = {
                                Text(
                                    text = "ابحث بالاسم أو الفئة (مساعد، لغات، تقني...)",
                                    color = SlateGray,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "بحث",
                                    tint = if (searchQuery.isNotBlank()) CyberViolet else SlateGray
                                )
                            },
                            trailingIcon = {
                                AnimatedVisibility(
                                    visible = searchQuery.isNotBlank(),
                                    enter = fadeIn(),
                                    exit = fadeOut()
                                ) {
                                    IconButton(
                                        onClick = { searchQuery = "" },
                                        modifier = Modifier.testTag("clear_search_button")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "مسح البحث",
                                            tint = SlateGray
                                        )
                                    }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .testTag("persistent_search_bar"),
                            shape = RoundedCornerShape(26.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyberViolet,
                                unfocusedBorderColor = ObsidianCardBorder,
                                focusedTextColor = PureWhite,
                                unfocusedTextColor = PureWhite,
                                focusedContainerColor = ObsidianCard,
                                unfocusedContainerColor = ObsidianCard
                            ),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Persistent Category Filter Chips Row
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.testTag("gallery_category_chips")
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
                                    selectedContainerColor = CyberVioletDark,
                                    selectedLabelColor = PureWhite
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = ObsidianCardBorder,
                                    selectedBorderColor = CyberViolet
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("persona_gallery_screen")
        ) {
            if (filteredPersonas.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "لا توجد نتائج",
                            tint = SlateGray,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "لم يتم العثور على شخصيات تطابق: \"$searchQuery\"",
                            style = MaterialTheme.typography.titleMedium,
                            color = PureWhite,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "جرّب البحث باسم شخصية مثل 'نوفا' أو 'ليلى' أو بفئة مثل 'مساعد' أو 'تقني'",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateGray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("persona_gallery_grid")
                ) {
                    items(filteredPersonas, key = { it.id }) { persona ->
                        PersonaGalleryItemCard(
                            persona = persona,
                            onStartCall = { onStartCall(persona.id) },
                            onOpenChat = { onOpenChat(persona.id) },
                            modifier = Modifier.testTag("gallery_item_${persona.id}")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PersonaGalleryItemCard(
    persona: Persona,
    onStartCall: () -> Unit,
    onOpenChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable { onStartCall() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = ObsidianCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon / Avatar with online badge
            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = painterResource(id = persona.avatarRes),
                    contentDescription = persona.nameAr,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.5.dp, CyberViolet, RoundedCornerShape(18.dp)),
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

            Spacer(modifier = Modifier.height(10.dp))

            // Name
            Text(
                text = persona.nameAr,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PureWhite,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Role
            Text(
                text = persona.roleAr,
                style = MaterialTheme.typography.labelSmall,
                color = NeonCyan,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Brief Description
            Text(
                text = persona.bioAr,
                style = MaterialTheme.typography.bodySmall,
                color = SlateGray,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Call & Chat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = onStartCall,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CyberViolet)
                        .testTag("gallery_call_${persona.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "مكالمة فيديو",
                        tint = PureWhite,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onOpenChat,
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x223B82F6))
                        .border(1.dp, ElectricBlue, RoundedCornerShape(10.dp))
                        .testTag("gallery_chat_${persona.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "دردشة نصية",
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

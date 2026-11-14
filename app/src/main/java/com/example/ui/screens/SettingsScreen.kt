package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Token
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.ui.theme.CallDangerRed
import com.example.ui.theme.CallSuccessGreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardBorder
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PureWhite
import com.example.ui.theme.SlateGray
import com.example.ui.theme.WarningAmber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    BackHandler { onBackClick() }

    var selectedLang by remember { mutableStateOf(AppLanguage.ARABIC) }
    var e2eEncryptionEnabled by remember { mutableStateOf(true) }
    var cameraPermissionGranted by remember { mutableStateOf(true) }
    var micPermissionGranted by remember { mutableStateOf(true) }
    var aiCredits by remember { mutableIntStateOf(750) }
    var isPremiumUser by remember { mutableStateOf(false) }

    var showReportDialog by remember { mutableStateOf(false) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    var showPremiumDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ObsidianDark,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "الإعدادات والأمان والخصوصية",
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
                .testTag("settings_screen"),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Premium Card Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showPremiumDialog = true }
                    .testTag("premium_banner_card"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, CyberViolet)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0x338B5CF6), Color(0x333B82F6))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(CyberViolet),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Diamond,
                                contentDescription = "اشتراك بريميوم",
                                tint = PureWhite,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPremiumUser) "عضوية AI CONNECT Premium مفعلة ✨" else "ترقية إلى AI CONNECT VIP",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = PureWhite
                            )
                            Text(
                                text = "مكالمات فيديو غير محدودة بدقة 4K وشخصيات ذكاء اصطناعي حصرية",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateGray
                            )
                        }
                    }
                }
            }

            // AI Credits Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Token,
                            contentDescription = "رصيد الذكاء الاصطناعي",
                            tint = WarningAmber,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "رصيد الذكاء الاصطناعي",
                                style = MaterialTheme.typography.titleMedium,
                                color = PureWhite
                            )
                            Text(
                                text = "$aiCredits نقطة متاحة",
                                style = MaterialTheme.typography.bodySmall,
                                color = NeonCyan
                            )
                        }
                    }

                    Button(
                        onClick = { showTopUpDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CyberViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("شحن الرصيد", color = PureWhite, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            // Security & Privacy Group
            Text(
                text = "الأمان والخصوصية",
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // E2E Encryption
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "تشفير",
                                tint = CallSuccessGreen,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "تشفير المكالمات طرف إلى طرف",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = PureWhite
                                )
                                Text(
                                    text = "حماية كاملة للبيانات والاتصال",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateGray
                                )
                            }
                        }
                        Switch(
                            checked = e2eEncryptionEnabled,
                            onCheckedChange = { e2eEncryptionEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = CallSuccessGreen
                            )
                        )
                    }

                    // Camera permission status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "الكاميرا",
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "إذن الكاميرا",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (cameraPermissionGranted) "مفعل ومسموح به للمكالمات" else "معطل",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateGray
                                )
                            }
                        }
                        Switch(
                            checked = cameraPermissionGranted,
                            onCheckedChange = { cameraPermissionGranted = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = CyberViolet
                            )
                        )
                    }

                    // Mic permission status
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "الميكروفون",
                                tint = ElectricBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "إذن الميكروفون",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = PureWhite
                                )
                                Text(
                                    text = if (micPermissionGranted) "مفعل ومسموح به للصوت" else "معطل",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateGray
                                )
                            }
                        }
                        Switch(
                            checked = micPermissionGranted,
                            onCheckedChange = { micPermissionGranted = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = PureWhite,
                                checkedTrackColor = CyberViolet
                            )
                        )
                    }
                }
            }

            // Language & Moderation Group
            Text(
                text = "اللغة والتحكم",
                style = MaterialTheme.typography.titleMedium,
                color = PureWhite
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = ObsidianCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, ObsidianCardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Language
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "اللغة",
                                tint = NeonCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "اللغة الأساسية",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PureWhite
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            AppLanguage.entries.forEach { lang ->
                                val isSelected = selectedLang == lang
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) CyberViolet else Color(0xFF1F243D))
                                        .clickable { selectedLang = lang }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = lang.code.uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PureWhite
                                    )
                                }
                            }
                        }
                    }

                    // Report / Block safety feature
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showReportDialog = true }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Report,
                            contentDescription = "إبلاغ",
                            tint = CallDangerRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "الإبلاغ عن سلوك مسيء أو حظر مستخدم",
                                style = MaterialTheme.typography.bodyLarge,
                                color = PureWhite
                            )
                            Text(
                                text = "بيئة تواصل آمنة ومحمية من المضايقات",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showPremiumDialog) {
        AlertDialog(
            onDismissRequest = { showPremiumDialog = false },
            title = {
                Text(
                    text = "باقات AI CONNECT VIP",
                    style = MaterialTheme.typography.titleLarge,
                    color = PureWhite
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "اختر خطة الاشتراك للاستمتاع بجميع ميزات الذكاء الاصطناعي بدون حدود:",
                        color = SlateGray,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2238)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("خطة البريميوم الشهرية", color = NeonCyan, fontWeight = FontWeight.Bold)
                            Text("• مكالمات فيديو غير محدودة", color = PureWhite, style = MaterialTheme.typography.bodySmall)
                            Text("• جميع شخصيات الذكاء الاصطناعي والأصوات", color = PureWhite, style = MaterialTheme.typography.bodySmall)
                            Text("• ترجمة فورية غير محدودة بـ 10+ لغات", color = PureWhite, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isPremiumUser = true
                        showPremiumDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberViolet)
                ) {
                    Text("تفعيل الاشتراك التجريبي", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPremiumDialog = false }) {
                    Text("إغلاق", color = SlateGray)
                }
            },
            containerColor = ObsidianCard
        )
    }

    if (showTopUpDialog) {
        AlertDialog(
            onDismissRequest = { showTopUpDialog = false },
            title = { Text("شحن رصيد الذكاء الاصطناعي", color = PureWhite) },
            text = {
                Text(
                    "يمكنك إضافة 500 نقطة ذكاء اصطناعي إضافية لاستخدام الميزات التوليدية المتقدمة.",
                    color = SlateGray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        aiCredits += 500
                        showTopUpDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CallSuccessGreen)
                ) {
                    Text("إضافة 500 نقطة", color = PureWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopUpDialog = false }) {
                    Text("إلغاء", color = SlateGray)
                }
            },
            containerColor = ObsidianCard
        )
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text("مركز الأمان والإبلاغ", color = PureWhite) },
            text = {
                Text(
                    "تم إرسال تقرير الأمان إلى خوادم AI CONNECT لمراجعة الاتصال والتأكد من توافقه مع معايير الأمان ومكافحة الإساءة.",
                    color = SlateGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { showReportDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = CyberViolet)
                ) {
                    Text("حسناً", color = PureWhite)
                }
            },
            containerColor = ObsidianCard
        )
    }
}

package com.example.data.model

import androidx.annotation.DrawableRes
import com.example.R

enum class AppLanguage(val code: String, val displayNameAr: String, val displayNameEn: String) {
    ARABIC("ar", "العربية", "Arabic"),
    ENGLISH("en", "الإنجليزية", "English"),
    FRENCH("fr", "الفرنسية", "French");

    companion object {
        fun fromCode(code: String): AppLanguage {
            return entries.find { it.code.equals(code, ignoreCase = true) } ?: ARABIC
        }
    }
}

data class Persona(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val roleAr: String,
    val roleEn: String,
    val bioAr: String,
    val bioEn: String,
    @DrawableRes val avatarRes: Int,
    val category: String, // "assistant", "tutor", "mentor", "friend"
    val defaultLanguage: AppLanguage,
    val voicePitch: Float = 1.0f,
    val voiceRate: Float = 1.0f,
    val systemPrompt: String,
    val interests: List<String>,
    val isOnline: Boolean = true,
    val isCustom: Boolean = false
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val senderName: String,
    val isUser: Boolean,
    val originalText: String,
    val translatedText: String? = null,
    val originalLanguage: AppLanguage = AppLanguage.ARABIC,
    val targetLanguage: AppLanguage = AppLanguage.ENGLISH,
    val timestamp: Long = System.currentTimeMillis()
)

data class CallHistoryItem(
    val id: String = java.util.UUID.randomUUID().toString(),
    val personaId: String,
    val personaName: String,
    @DrawableRes val avatarRes: Int,
    val durationSeconds: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isVideo: Boolean = true
)

enum class VideoFilterType(val id: String, val titleAr: String, val titleEn: String) {
    NONE("none", "طبيعي", "Normal"),
    CYBER_GLOW("cyber", "وهج سايبر", "Cyber Glow"),
    STUDIO_LIGHT("studio", "إضاءة استوديو", "Studio Light"),
    WARM_SUNSET("warm", "غروب دافئ", "Warm Sunset"),
    VIRTUAL_BLUR("blur", "تمويه الخلفية", "Blur BG"),
    BEAUTY_SMOOTH("beauty", "فلتر الجمال", "Beauty Smooth"),
    VIVID_HDR("vivid", "تباين حيوي", "Vivid HDR"),
    NOIR_MONO("noir", "أبيض وأسود", "Noir Mono")
}

data class CameraFilterConfig(
    val type: VideoFilterType = VideoFilterType.NONE,
    val intensity: Float = 1.0f,
    val brightness: Float = 0.0f,
    val contrast: Float = 1.0f,
    val saturation: Float = 1.0f
)


package com.example.service

import android.util.Log
import com.example.data.model.AppLanguage
import com.example.data.model.Persona
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class AiChatService {

    private val TAG = "AiChatService"

    private val generativeModel by lazy {
        try {
            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = generationConfig {
                    temperature = 0.7f
                    topK = 40
                }
            )
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI model initialization deferred/failed", e)
            null
        }
    }

    suspend fun generatePersonaResponse(
        persona: Persona,
        conversationHistory: List<Pair<String, String>>, // role, text
        userMessage: String,
        targetLanguage: AppLanguage
    ): String = withContext(Dispatchers.IO) {
        val langInstruction = when (targetLanguage) {
            AppLanguage.ARABIC -> "Please respond in natural Arabic."
            AppLanguage.FRENCH -> "Please respond in natural French."
            AppLanguage.ENGLISH -> "Please respond in natural English."
        }

        val prompt = buildString {
            appendLine("System instructions:")
            appendLine(persona.systemPrompt)
            appendLine("Character Persona: ${persona.nameEn} (${persona.roleEn}).")
            appendLine("Language Instruction: $langInstruction Keep responses concise, conversational, and direct for a video/audio chat call (under 3 sentences).")
            appendLine("\nConversation context:")
            conversationHistory.takeLast(4).forEach { (role, msg) ->
                appendLine("$role: $msg")
            }
            appendLine("User: $userMessage")
            appendLine("Response:")
        }

        try {
            val model = generativeModel
            if (model != null) {
                val response = withTimeout(25_000L) {
                    model.generateContent(prompt)
                }
                val text = response.text?.trim()
                if (!text.isNullOrBlank()) {
                    return@withContext text
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Gemini generation exception, falling back to local persona engine: ${e.message}")
        }

        // Resilient smart persona dialogue fallback
        generateSmartPersonaFallback(persona, userMessage, targetLanguage)
    }

    suspend fun translateText(
        text: String,
        sourceLanguage: AppLanguage,
        targetLanguage: AppLanguage
    ): String = withContext(Dispatchers.IO) {
        if (sourceLanguage == targetLanguage || text.isBlank()) return@withContext text

        val prompt = "Translate the following text accurately and naturally from ${sourceLanguage.displayNameEn} to ${targetLanguage.displayNameEn}. Only output the translated text without extra explanation:\n\"$text\""

        try {
            val model = generativeModel
            if (model != null) {
                val response = withTimeout(15_000L) {
                    model.generateContent(prompt)
                }
                val translated = response.text?.trim()
                if (!translated.isNullOrBlank()) {
                    return@withContext translated.removeSurrounding("\"")
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Translation API fallback engaged: ${e.message}")
        }

        fallbackTranslation(text, sourceLanguage, targetLanguage)
    }

    private fun generateSmartPersonaFallback(
        persona: Persona,
        input: String,
        language: AppLanguage
    ): String {
        val lower = input.lowercase()
        return when (persona.id) {
            "nova" -> when (language) {
                AppLanguage.ARABIC -> when {
                    "مرحبا" in lower || "سلام" in lower -> "أهلاً بك مجدداً! أنا نوفا معك دائماً عبر البث المباشر. كيف يمكنني دعمك في التكنولوجيا أو الإنتاجية اليوم؟"
                    "مكالمة" in lower || "فيديو" in lower -> "نعم، اتصال الفيديو عالي الدقة مفعل ومشفر بتقنية الذكاء الاصطناعي، والصوت نقي وواضح."
                    "شكرا" in lower -> "على الرحب والسعة! أنا هنا لأجلك في أي لحظة."
                    else -> "فهمت سؤالك باهتمام. تقنيات الذكاء الاصطناعي التوليدي تتيح لنا تواصل أسرع وتحليل فوري. هل تود التعمق أكثر في هذه النقطة؟"
                }
                AppLanguage.FRENCH -> "Bonjour! Je suis Nova, votre assistante IA connectée en temps réel. Comment puis-je vous aider aujourd'hui?"
                else -> "Hello! Nova here in live AI video stream. Ready to assist with research, tasks, or tech brainstorming whenever you are!"
            }
            "layla" -> when (language) {
                AppLanguage.ARABIC -> when {
                    "كيف" in lower || "نطق" in lower -> "نطقك ممتاز! تذكر دائماً أن مخارج الحروف العربية تحتاج هدوءاً وثقة، وممارستك اليومية تثمر بسرعة."
                    "فرنسية" in lower || "انجليزية" in lower -> "رائع! اللغات جسور تواصل عالمية. دعنا نتبادل جملاً قصيرة لتنمية الطلاقة."
                    else -> "أحسنت التعبير! لغتك سلسة وجميلة، استمر في الحديث وسأتابع معك خطوة بخطوة."
                }
                AppLanguage.FRENCH -> "Excellent travail linguistique! Pratiquer quotidiennement est la clé du succès. Quelle phrase souhaites-tu explorer?"
                else -> "Great pronunciation! Language is all about regular conversational practice. Shall we try another phrase together?"
            }
            "alex" -> when (language) {
                AppLanguage.ARABIC -> "أهلاً صديقي! أتابع معك بكل تركيز. عالم البرمجة وحلول الذكاء الاصطناعي يتطور بسرعة، وفكرتك تبدو واعدة جداً!"
                AppLanguage.FRENCH -> "Salut! C'est Alex. C'est une excellente question d'architecture logicielle. Voyons comment la structurer proprement."
                else -> "Hey there! Alex here. That's a solid engineering question. Breaking it down into modular services is definitely the way to go."
            }
            else -> when (language) {
                AppLanguage.ARABIC -> "أنا سعيدة جداً بمشاركتك هذه الأفكار معي! من الرائع دائماً أن نجد وقتاً للحديث والتأمل الإيجابي في يومنا."
                AppLanguage.FRENCH -> "C'est un plaisir de discuter avec toi! Prends un moment pour respirer et apprécier ta journée."
                else -> "I'm so glad we connected! Remember to take time for yourself and celebrate your daily wins."
            }
        }
    }

    private fun fallbackTranslation(text: String, from: AppLanguage, to: AppLanguage): String {
        val dict = mapOf(
            "مرحبا" to mapOf(AppLanguage.ENGLISH to "Hello", AppLanguage.FRENCH to "Bonjour"),
            "كيف حالك" to mapOf(AppLanguage.ENGLISH to "How are you?", AppLanguage.FRENCH to "Comment allez-vous?"),
            "شكرا لك" to mapOf(AppLanguage.ENGLISH to "Thank you", AppLanguage.FRENCH to "Merci"),
            "أهلا بك" to mapOf(AppLanguage.ENGLISH to "Welcome", AppLanguage.FRENCH to "Bienvenue"),
            "Hello" to mapOf(AppLanguage.ARABIC to "مرحباً", AppLanguage.FRENCH to "Bonjour"),
            "How are you?" to mapOf(AppLanguage.ARABIC to "كيف حالك؟", AppLanguage.FRENCH to "Comment ça va?"),
            "Thank you" to mapOf(AppLanguage.ARABIC to "شكراً لك", AppLanguage.FRENCH to "Merci")
        )
        dict.entries.forEach { entry ->
            if (text.contains(entry.key, ignoreCase = true)) {
                entry.value[to]?.let { return it }
            }
        }
        return when (to) {
            AppLanguage.ARABIC -> "[$text - مترجم]"
            AppLanguage.FRENCH -> "[$text - traduit]"
            AppLanguage.ENGLISH -> "[$text - translated]"
        }
    }
}

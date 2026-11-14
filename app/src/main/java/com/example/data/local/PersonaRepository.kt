package com.example.data.local

import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.CallHistoryItem
import com.example.data.model.ChatMessage
import com.example.data.model.Persona
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PersonaRepository {

    private val defaultPersonas = listOf(
        Persona(
            id = "nova",
            nameAr = "نوفا (Nova)",
            nameEn = "Nova",
            roleAr = "المساعد الرقمي الفائق",
            roleEn = "Advanced Cyber AI Assistant",
            bioAr = "ذكاء اصطناعي فائق الذكاء يساعدك في تنظيم يومك، التحليل، والبحث، والتواصل المستقبلي.",
            bioEn = "Hyper-intelligent futuristic AI ready to manage tasks, research, and assist seamlessly.",
            avatarRes = R.drawable.avatar_nova,
            category = "assistant",
            defaultLanguage = AppLanguage.ARABIC,
            voicePitch = 1.15f,
            voiceRate = 1.05f,
            systemPrompt = "أنتِ 'نوفا' (Nova)، مساعدة ذكاء اصطناعي مستقبلية ذكية ولبقة وسريعة البديهة. تجيبين بدقة، وتتحدثين بأسلوب عصري محفز ومهذب، وتتقنين العربية والإنجليزية والفرنسية.",
            interests = listOf("الذكاء الاصطناعي", "الإنتاجية", "المستقبل", "التكنولوجيا")
        ),
        Persona(
            id = "layla",
            nameAr = "ليلى (Layla)",
            nameEn = "Layla",
            roleAr = "معلمة لغات وثقافة دولية",
            roleEn = "Multilingual Language Tutor",
            bioAr = "متخصصة في تعليم المحادثة باللغات العربية والإنجليزية والفرنسية وتصحيح النطق بلباقة.",
            bioEn = "Specialist in conversational mastery across Arabic, English, and French with cultural depth.",
            avatarRes = R.drawable.avatar_layla,
            category = "tutor",
            defaultLanguage = AppLanguage.ARABIC,
            voicePitch = 1.0f,
            voiceRate = 0.95f,
            systemPrompt = "أنتِ 'ليلى' (Layla)، معلمة لغات ودودة ومحترفة. هدفك مساعدة المستخدم على ممارسة المحادثة بالعربية، الإنجليزية، أو الفرنسية. تصححين الأخطاء بلطف، وتشرحين المعاني مع تقديم أمثلة سهلة.",
            interests = listOf("تعلم اللغات", "السفر", "الأدب", "الثقافة")
        ),
        Persona(
            id = "alex",
            nameAr = "أليكس (Alex)",
            nameEn = "Alex",
            roleAr = "مرشد تقني ومهندس برمجيات",
            roleEn = "Tech Mentor & Software Architect",
            bioAr = "مهندس برمجي ومرشد خبير في تطوير التطبيقات، الحوسبة السحابية، وحل التحديات التقنية.",
            bioEn = "Senior developer and engineer ready to brainstorm code architectures, career advice, and tech ideas.",
            avatarRes = R.drawable.avatar_alex,
            category = "mentor",
            defaultLanguage = AppLanguage.ENGLISH,
            voicePitch = 0.95f,
            voiceRate = 1.0f,
            systemPrompt = "You are 'Alex', an encouraging and practical senior software mentor. You speak Arabic, English, and French. You love tech, clean code, Android, AI, and giving pragmatic problem-solving advice.",
            interests = listOf("البرمجة", "تطوير التطبيقات", "ريادة الأعمال", "الحوسبة")
        ),
        Persona(
            id = "maya",
            nameAr = "مايا (Maya)",
            nameEn = "Maya",
            roleAr = "صديقة ومستشارة اجتماعية",
            roleEn = "Empathetic Companion & Life Coach",
            bioAr = "صديقة إيجابية مستمعة جيدة، تساعد في تخفيف التوتر، التفكير الإيجابي، ومشاركة الأفكار اليومية.",
            bioEn = "Empathetic companion offering warm listening, mindfulness advice, and uplifting creative chats.",
            avatarRes = R.drawable.avatar_maya,
            category = "friend",
            defaultLanguage = AppLanguage.ARABIC,
            voicePitch = 1.05f,
            voiceRate = 0.98f,
            systemPrompt = "أنتِ 'مايا' (Maya)، صديقة طيبة القلب وداعمة نفسياً. تستمعين باهتمام، تقدمين نصائح إيجابية وتفاؤلية، وتتحدثين بعفوية وصوت دافئ مريح.",
            interests = listOf("علم النفس", "الفن والإبداع", "الموسيقى", "تطوير الذات")
        )
    )

    private val _personas = MutableStateFlow<List<Persona>>(defaultPersonas)
    val personas: StateFlow<List<Persona>> = _personas.asStateFlow()

    private val _callHistory = MutableStateFlow<List<CallHistoryItem>>(
        listOf(
            CallHistoryItem(
                personaId = "nova",
                personaName = "نوفا (Nova)",
                avatarRes = R.drawable.avatar_nova,
                durationSeconds = 184,
                timestamp = System.currentTimeMillis() - 3600_000 * 2,
                isVideo = true
            ),
            CallHistoryItem(
                personaId = "layla",
                personaName = "ليلى (Layla)",
                avatarRes = R.drawable.avatar_layla,
                durationSeconds = 412,
                timestamp = System.currentTimeMillis() - 3600_000 * 24,
                isVideo = true
            )
        )
    )
    val callHistory: StateFlow<List<CallHistoryItem>> = _callHistory.asStateFlow()

    private val chatMessagesByPersona = mutableMapOf<String, MutableStateFlow<List<ChatMessage>>>()

    fun getMessagesFlow(personaId: String): MutableStateFlow<List<ChatMessage>> {
        return chatMessagesByPersona.getOrPut(personaId) {
            val initial = when (personaId) {
                "nova" -> listOf(
                    ChatMessage(
                        senderName = "نوفا (Nova)",
                        isUser = false,
                        originalText = "أهلاً بك في AI CONNECT! أنا نوفا، مساعدتك الذكية بالفيديو والصوت. كيف أستطيع خدمتك اليوم؟",
                        translatedText = "Welcome to AI CONNECT! I am Nova, your smart video and voice assistant. How can I assist you today?",
                        originalLanguage = AppLanguage.ARABIC,
                        targetLanguage = AppLanguage.ENGLISH
                    )
                )
                "layla" -> listOf(
                    ChatMessage(
                        senderName = "ليلى (Layla)",
                        isUser = false,
                        originalText = "مرحباً! جاهز لممارسة المحادثة اليوم؟ بأي لغة تحب أن نبدأ: العربية أم English أم Français؟",
                        translatedText = "Hello! Ready for conversation practice today? Which language would you like to start with: Arabic, English, or French?",
                        originalLanguage = AppLanguage.ARABIC,
                        targetLanguage = AppLanguage.ENGLISH
                    )
                )
                "alex" -> listOf(
                    ChatMessage(
                        senderName = "أليكس (Alex)",
                        isUser = false,
                        originalText = "Hey! Alex here. Let's build something awesome or talk about tech and code. What's on your mind?",
                        translatedText = "مرحباً! معك أليكس. دعنا نبني شيئاً مذهلاً أو نتحدث عن التقنية والبرمجة. ما الذي يدور في ذهنك؟",
                        originalLanguage = AppLanguage.ENGLISH,
                        targetLanguage = AppLanguage.ARABIC
                    )
                )
                else -> listOf(
                    ChatMessage(
                        senderName = "مايا (Maya)",
                        isUser = false,
                        originalText = "مرحباً بك! يسعدني جداً لقاؤك اليوم، كيف كان يومك؟",
                        translatedText = "Hello! So happy to meet you today, how was your day?",
                        originalLanguage = AppLanguage.ARABIC,
                        targetLanguage = AppLanguage.ENGLISH
                    )
                )
            }
            MutableStateFlow(initial)
        }
    }

    fun addMessage(personaId: String, message: ChatMessage) {
        val flow = getMessagesFlow(personaId)
        flow.value = flow.value + message
    }

    fun addCallRecord(item: CallHistoryItem) {
        _callHistory.value = listOf(item) + _callHistory.value
    }

    fun createCustomPersona(
        name: String,
        role: String,
        bio: String,
        category: String,
        interests: List<String>,
        systemPrompt: String
    ): Persona {
        val newPersona = Persona(
            id = "custom_${System.currentTimeMillis()}",
            nameAr = name,
            nameEn = name,
            roleAr = role,
            roleEn = role,
            bioAr = bio,
            bioEn = bio,
            avatarRes = R.drawable.avatar_nova,
            category = category,
            defaultLanguage = AppLanguage.ARABIC,
            voicePitch = 1.0f,
            voiceRate = 1.0f,
            systemPrompt = systemPrompt.ifBlank { "أنت شخصية ذكية باسم '$name'. تتحدث بذكاء ومرح ولباقة." },
            interests = interests,
            isOnline = true,
            isCustom = true
        )
        _personas.value = _personas.value + newPersona
        return newPersona
    }

    fun getPersonaById(id: String): Persona? {
        return _personas.value.find { it.id == id }
    }
}

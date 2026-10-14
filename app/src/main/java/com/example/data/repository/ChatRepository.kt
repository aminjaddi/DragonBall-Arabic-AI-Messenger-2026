package com.example.data.repository

import com.example.data.local.ChatDao
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.GeminiApiClient
import com.example.data.remote.Part
import com.example.model.Character
import com.example.model.CharacterData
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.util.SafetyFilter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class ChatRepository(
    private val chatDao: ChatDao,
    private val settingsRepository: SettingsRepository
) {

    fun getMessages(characterId: String): Flow<List<ChatMessage>> {
        return chatDao.getMessagesForCharacter(characterId)
    }

    suspend fun initializeCharacterIfEmpty(character: Character) {
        withContext(Dispatchers.IO) {
            val recent = chatDao.getRecentMessages(character.id, 1)
            if (recent.isEmpty()) {
                val greeting = ChatMessage(
                    characterId = character.id,
                    sender = MessageSender.CHARACTER,
                    content = character.initialGreetingAr,
                    timestamp = System.currentTimeMillis()
                )
                chatDao.insertMessage(greeting)
            }
        }
    }

    suspend fun sendMessage(
        character: Character,
        userText: String,
        onStartGenerating: () -> Unit,
        onFinished: (Boolean) -> Unit
    ) = withContext(Dispatchers.IO) {
        // 1. Safety Validation
        val validation = SafetyFilter.validateMessage(userText)
        if (!validation.isClean) {
            val systemWarning = ChatMessage(
                characterId = character.id,
                sender = MessageSender.SYSTEM,
                content = validation.errorMessage ?: "تنبيه أمان: تم حظر الرسالة.",
                timestamp = System.currentTimeMillis(),
                isError = true
            )
            chatDao.insertMessage(systemWarning)
            onFinished(false)
            return@withContext
        }

        // 2. Insert User Message into local database
        val cleanText = validation.sanitizedMessage ?: userText
        val userMessage = ChatMessage(
            characterId = character.id,
            sender = MessageSender.USER,
            content = cleanText,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(userMessage)

        // 3. Notify UI that AI generation has started
        withContext(Dispatchers.Main) {
            onStartGenerating()
        }

        // 4. Retrieve recent message history for context (up to 12 turns)
        val historyList = chatDao.getRecentMessages(character.id, 12).reversed()
        val geminiContents = mutableListOf<Content>()

        for (msg in historyList) {
            when (msg.sender) {
                MessageSender.USER -> {
                    geminiContents.add(
                        Content(role = "user", parts = listOf(Part(text = msg.content)))
                    )
                }
                MessageSender.CHARACTER -> {
                    geminiContents.add(
                        Content(role = "model", parts = listOf(Part(text = msg.content)))
                    )
                }
                MessageSender.SYSTEM -> Unit
            }
        }

        // 5. Check API Key
        val apiKey = settingsRepository.getEffectiveApiKey()
        if (apiKey.isEmpty()) {
            val errorNotice = ChatMessage(
                characterId = character.id,
                sender = MessageSender.SYSTEM,
                content = "يرجى إضافة مفتاح Gemini API في الإعدادات لبدء المحادثة الذكية مع الشخصية.",
                timestamp = System.currentTimeMillis(),
                isError = true
            )
            chatDao.insertMessage(errorNotice)
            withContext(Dispatchers.Main) { onFinished(false) }
            return@withContext
        }

        // 6. Build Request without thinkingConfig so that all modern models (including gemini-flash-lite-latest) succeed instantly
        val request = GenerateContentRequest(
            contents = geminiContents,
            systemInstruction = Content(
                parts = listOf(Part(text = character.systemInstruction))
            ),
            generationConfig = GenerationConfig(
                temperature = 0.7f,
                topP = 0.9f,
                maxOutputTokens = 800,
                thinkingConfig = null
            )
        )

        // 7. Make Remote API Call with Multi-Model Cascade
        var responseText: String? = null
        var lastCallError: String? = null

        val primaryModel = settingsRepository.selectedModel.value
        val candidateModels = listOf(
            primaryModel,
            SettingsRepository.MODEL_FLASH_LITE_LATEST,
            SettingsRepository.MODEL_FLASH_3,
            SettingsRepository.MODEL_FLASH_35
        ).distinct()

        for (model in candidateModels) {
            for (attempt in 1..2) {
                try {
                    val response = GeminiApiClient.service.generateContent(
                        model = model,
                        apiKey = apiKey,
                        request = request
                    )
                    val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (!text.isNullOrBlank()) {
                        responseText = text
                        break
                    }
                } catch (e: Exception) {
                    lastCallError = e.localizedMessage ?: e.message
                    if (attempt < 2) {
                        delay(300)
                    }
                }
            }
            if (!responseText.isNullOrBlank()) {
                break
            }
        }

        // 8. Process and Store Result - Pure Genuine AI Response
        if (!responseText.isNullOrBlank()) {
            val sanitizedResponse = SafetyFilter.sanitizeAiOutput(responseText.trim())
            val characterReply = ChatMessage(
                characterId = character.id,
                sender = MessageSender.CHARACTER,
                content = sanitizedResponse,
                timestamp = System.currentTimeMillis()
            )
            chatDao.insertMessage(characterReply)
            withContext(Dispatchers.Main) { onFinished(true) }
        } else {
            // Provide explicit, honest error feedback instead of fake or random responses
            val errorDescription = lastCallError ?: "تعذر الوصول لخوادم الذكاء الاصطناعي"
            val errorNotice = ChatMessage(
                characterId = character.id,
                sender = MessageSender.SYSTEM,
                content = "فشل توليد الرد من الذكاء الاصطناعي ($errorDescription). يرجى التحقق من الاتصال والمحاولة مجددًا.",
                timestamp = System.currentTimeMillis(),
                isError = true
            )
            chatDao.insertMessage(errorNotice)
            withContext(Dispatchers.Main) { onFinished(false) }
        }
    }

    suspend fun testConnection(): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val apiKey = settingsRepository.getEffectiveApiKey()
        if (apiKey.isEmpty()) {
            return@withContext Pair(false, "لم يتم العثور على مفتاح API. يرجى إدخال المفتاح في الإعدادات.")
        }

        val testModel = settingsRepository.selectedModel.value
        val testRequest = GenerateContentRequest(
            contents = listOf(Content(role = "user", parts = listOf(Part(text = "مرحبا، أجب بنعم فقط")))),
            generationConfig = GenerationConfig(
                maxOutputTokens = 100,
                thinkingConfig = null
            )
        )

        return@withContext try {
            val startTime = System.currentTimeMillis()
            val response = GeminiApiClient.service.generateContent(
                model = testModel,
                apiKey = apiKey,
                request = testRequest
            )
            val elapsed = System.currentTimeMillis() - startTime
            val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!text.isNullOrBlank()) {
                Pair(true, "الاتصال بالذكاء الاصطناعي ناجح! الاستجابة خلال ${elapsed}ms عبر النموذج: $testModel")
            } else {
                Pair(false, "تم الاتصال ولكن لم يُرجع النموذج نصًا.")
            }
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: e.message ?: "خطأ غير معروف"
            Pair(false, "فشل الاتصال ($msg).")
        }
    }

    suspend fun clearChat(characterId: String) = withContext(Dispatchers.IO) {
        chatDao.clearMessagesForCharacter(characterId)
        val character = CharacterData.getCharacterById(characterId)
        val greeting = ChatMessage(
            characterId = character.id,
            sender = MessageSender.CHARACTER,
            content = character.initialGreetingAr,
            timestamp = System.currentTimeMillis()
        )
        chatDao.insertMessage(greeting)
    }

    suspend fun deleteMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        chatDao.deleteMessage(message)
    }

    suspend fun clearAll() = withContext(Dispatchers.IO) {
        chatDao.clearAllMessages()
    }
}

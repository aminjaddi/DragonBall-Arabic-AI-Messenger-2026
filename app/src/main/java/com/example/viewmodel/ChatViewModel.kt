package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.repository.ChatRepository
import com.example.data.repository.SettingsRepository
import com.example.model.Character
import com.example.model.CharacterData
import com.example.model.ChatMessage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object HomeChats : Screen()
    data object CharactersList : Screen()
    data class ChatConversation(val characterId: String) : Screen()
    data class CharacterDetail(val characterId: String) : Screen()
    data object Settings : Screen()
    data object About : Screen()
    data object PrivacyPolicy : Screen()
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    val settingsRepository = SettingsRepository(application)
    val chatRepository = ChatRepository(db.chatDao(), settingsRepository)

    val characters = CharacterData.characters

    private val _currentScreen = MutableStateFlow<Screen>(Screen.HomeChats)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<Screen>()

    private val _selectedCharacter = MutableStateFlow(CharacterData.characters.first())
    val selectedCharacter: StateFlow<Character> = _selectedCharacter.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _snackBarMessage = MutableStateFlow<String?>(null)
    val snackBarMessage: StateFlow<String?> = _snackBarMessage.asStateFlow()

    // Observe messages for selected character
    val currentMessages: StateFlow<List<ChatMessage>> = _selectedCharacter
        .flatMapLatest { char ->
            chatRepository.getMessages(char.id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Pre-initialize greetings for all characters so conversations start smoothly
        viewModelScope.launch {
            characters.forEach { char ->
                chatRepository.initializeCharacterIfEmpty(char)
            }
        }
    }

    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        } else if (_currentScreen.value !is Screen.HomeChats) {
            _currentScreen.value = Screen.HomeChats
            return true
        }
        return false
    }

    fun openChatWith(character: Character) {
        _selectedCharacter.value = character
        navigateTo(Screen.ChatConversation(character.id))
    }

    fun openCharacterDetail(character: Character) {
        _selectedCharacter.value = character
        navigateTo(Screen.CharacterDetail(character.id))
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isEmpty() || _isGenerating.value) return

        _inputText.value = ""
        val character = _selectedCharacter.value

        viewModelScope.launch {
            chatRepository.sendMessage(
                character = character,
                userText = text,
                onStartGenerating = {
                    _isGenerating.value = true
                },
                onFinished = { success ->
                    _isGenerating.value = false
                    if (!success) {
                        _snackBarMessage.value = "يرجى التحقق من الرسالة أو إعدادات الاتصال."
                    }
                }
            )
        }
    }

    fun clearCurrentChat() {
        viewModelScope.launch {
            chatRepository.clearChat(_selectedCharacter.value.id)
            _snackBarMessage.value = "تم مسح سجل المحادثة بنجاح."
        }
    }

    fun deleteMessage(message: ChatMessage) {
        viewModelScope.launch {
            chatRepository.deleteMessage(message)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            chatRepository.clearAll()
            characters.forEach { char ->
                chatRepository.initializeCharacterIfEmpty(char)
            }
            _snackBarMessage.value = "تمت إعادة تعيين كافة البيانات بنجاح."
        }
    }

    fun clearSnackBar() {
        _snackBarMessage.value = null
    }

    fun saveCustomApiKey(key: String) {
        settingsRepository.saveApiKey(key)
        _snackBarMessage.value = "تم حفظ مفتاح API بنجاح."
    }

    fun saveSelectedModel(model: String) {
        settingsRepository.saveModel(model)
        _snackBarMessage.value = "تم تحديث النموذج المعتمد."
    }

    fun saveAdmobBannerId(id: String) {
        settingsRepository.saveAdmobBannerId(id)
        _snackBarMessage.value = "تم تحديث معرّف إعلان AdMob."
    }

    fun toggleAds(enabled: Boolean) {
        settingsRepository.setAdsEnabled(enabled)
    }

    fun testApiConnection(onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val (success, message) = chatRepository.testConnection()
            onResult(success, message)
        }
    }
}

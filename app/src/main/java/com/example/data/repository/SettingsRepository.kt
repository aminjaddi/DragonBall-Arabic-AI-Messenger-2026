package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("db_messenger_settings", Context.MODE_PRIVATE)

    private val _customApiKey = MutableStateFlow(getSavedApiKey())
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow(getSavedModel())
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _admobBannerId = MutableStateFlow(getSavedBannerId())
    val admobBannerId: StateFlow<String> = _admobBannerId.asStateFlow()

    private val _adsEnabled = MutableStateFlow(getSavedAdsEnabled())
    val adsEnabled: StateFlow<Boolean> = _adsEnabled.asStateFlow()

    fun getEffectiveApiKey(): String {
        val userKey = _customApiKey.value.trim()
        if (userKey.isNotEmpty()) {
            return userKey
        }
        val buildKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }
        return if (buildKey.isNotBlank() && buildKey != "MY_GEMINI_API_KEY") buildKey else ""
    }

    fun saveApiKey(key: String) {
        prefs.edit().putString(KEY_CUSTOM_API_KEY, key.trim()).apply()
        _customApiKey.value = key.trim()
    }

    fun saveModel(model: String) {
        prefs.edit().putString(KEY_SELECTED_MODEL, model.trim()).apply()
        _selectedModel.value = model.trim()
    }

    fun saveAdmobBannerId(id: String) {
        prefs.edit().putString(KEY_ADMOB_BANNER, id.trim()).apply()
        _admobBannerId.value = id.trim()
    }

    fun setAdsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ADS_ENABLED, enabled).apply()
        _adsEnabled.value = enabled
    }

    private fun getSavedApiKey(): String = prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""

    private fun getSavedModel(): String {
        val saved = prefs.getString(KEY_SELECTED_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
        // Automatically migrate away from overloaded 503 or timing-out models to the rock-solid gemini-flash-lite-latest
        return if (saved == "gemini-flash-latest" || 
                   saved == "gemini-3.1-flash-lite-preview" || 
                   saved == "gemini-2.5-flash" ||
                   saved == "gemini-3.5-flash") {
            DEFAULT_MODEL
        } else {
            saved
        }
    }

    private fun getSavedBannerId(): String = prefs.getString(KEY_ADMOB_BANNER, DEFAULT_BANNER_ID) ?: DEFAULT_BANNER_ID
    private fun getSavedAdsEnabled(): Boolean = prefs.getBoolean(KEY_ADS_ENABLED, true)

    companion object {
        private const val KEY_CUSTOM_API_KEY = "custom_gemini_api_key"
        private const val KEY_SELECTED_MODEL = "selected_model"
        private const val KEY_ADMOB_BANNER = "admob_banner_id"
        private const val KEY_ADS_ENABLED = "ads_enabled"

        // Production-verified reliable AI models
        const val MODEL_FLASH_LITE_LATEST = "gemini-flash-lite-latest"
        const val MODEL_FLASH_3 = "gemini-3-flash-preview"
        const val MODEL_FLASH_35 = "gemini-3.5-flash"

        const val DEFAULT_MODEL = MODEL_FLASH_LITE_LATEST
        const val FALLBACK_MODEL = MODEL_FLASH_3

        // Official Google AdMob test banner ID for testing and demonstration
        const val DEFAULT_BANNER_ID = "ca-app-pub-3940256099942544/6300978111"
    }
}

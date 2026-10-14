package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdsClick
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.SettingsRepository
import com.example.ui.components.DragonBallTopBar
import com.example.ui.theme.DbGoldAccent
import com.example.ui.theme.DbOrangePrimary
import com.example.ui.theme.DbShenronGreen
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.Screen

@Composable
fun SettingsScreen(viewModel: ChatViewModel) {
    val context = LocalContext.current
    val customKey by viewModel.settingsRepository.customApiKey.collectAsState()
    val selectedModel by viewModel.settingsRepository.selectedModel.collectAsState()
    val bannerId by viewModel.settingsRepository.admobBannerId.collectAsState()
    val adsEnabled by viewModel.settingsRepository.adsEnabled.collectAsState()

    var keyInput by remember(customKey) { mutableStateOf(customKey) }
    var keyVisible by remember { mutableStateOf(false) }
    var bannerInput by remember(bannerId) { mutableStateOf(bannerId) }
    var showResetDialog by remember { mutableStateOf(false) }
    var isTestingConnection by remember { mutableStateOf(false) }
    var connectionTestResult by remember { mutableStateOf<String?>(null) }
    var connectionTestSuccess by remember { mutableStateOf<Boolean?>(null) }
    val clipboardManager = LocalClipboardManager.current

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            DragonBallTopBar(
                title = "الإعدادات",
                subtitle = "الذكاء الاصطناعي والإعلانات والبيانات",
                onBackClick = { viewModel.navigateBack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("settings_scroll_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. AI API Key Configuration
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Key, contentDescription = null, tint = DbOrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مفتاح الذكاء الاصطناعي (Gemini API)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "يدعم التطبيق مفتاح المنصة الافتراضي تلقائيًا. كما يمكنك إدخال مفتاحك المجاني الخاص من Google AI Studio لزيادة حدود الاستخدام وضمان اتصال دائم.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = keyInput,
                            onValueChange = { keyInput = it },
                            label = { Text("مفتاح Gemini API") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_api_key"),
                            visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { keyVisible = !keyVisible }) {
                                    Icon(
                                        imageVector = if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "إظهار/إخفاء المفتاح"
                                    )
                                }
                            },
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    viewModel.saveCustomApiKey(keyInput)
                                    Toast.makeText(context, "تم حفظ المفتاح بنجاح", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("btn_save_key")
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("حفظ المفتاح")
                            }

                            if (keyInput.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        keyInput = ""
                                        viewModel.saveCustomApiKey("")
                                        Toast.makeText(context, "تم مسح المفتاح المخصص والعودة للمفتاح الافتراضي", Toast.LENGTH_SHORT).show()
                                    }
                                ) {
                                    Text("إعادة ضبط")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val activeKey = viewModel.settingsRepository.getEffectiveApiKey()
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = if (activeKey.isNotEmpty()) DbShenronGreen else DbGoldAccent,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = if (activeKey.isNotEmpty()) "حالة المفتاح: مهيأ وجاهز للاستخدام" else "تنبيه: يمكنك استخدام محاكي الشخصيات المدمج مباشرة أو إضافة مفتاح Google لردود السحابة",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (activeKey.isNotEmpty()) DbShenronGreen else DbGoldAccent
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Test Connection Button
                        OutlinedButton(
                            onClick = {
                                isTestingConnection = true
                                connectionTestResult = null
                                viewModel.testApiConnection { success, message ->
                                    isTestingConnection = false
                                    connectionTestSuccess = success
                                    connectionTestResult = message
                                }
                            },
                            enabled = !isTestingConnection,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_test_connection")
                        ) {
                            if (isTestingConnection) {
                                CircularProgressIndicator(
                                    modifier = Modifier.padding(end = 8.dp),
                                    strokeWidth = 2.dp
                                )
                                Text("جاري فحص الاتصال بالخادم…")
                            } else {
                                Icon(Icons.Default.NetworkCheck, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("اختبار الاتصال بالخادم (Ping API)")
                            }
                        }

                        connectionTestResult?.let { resultText ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = resultText,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (connectionTestSuccess == true) DbShenronGreen else MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Free Key Quick Link
                        TextButton(
                            onClick = {
                                val url = "https://aistudio.google.com/app/apikey"
                                clipboardManager.setText(AnnotatedString(url))
                                Toast.makeText(context, "تم نسخ رابط إنشاء مفتاح مجاني: $url", Toast.LENGTH_LONG).show()
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                    context.startActivity(intent)
                                } catch (e: Exception) {
                                    // Clipboard copied as fallback
                                }
                            }
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("الحصول على مفتاح مجاني من Google AI Studio (بدون بطاقة دفع)")
                        }
                    }
                }
            }

            // 2. AI Model Selection
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = DbOrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "نموذج الذكاء الاصطناعي وخوادم الاستجابة",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "اختر نموذج الذكاء الاصطناعي الأساسي. يعمل تطبيقك بنماذج Google Gemini الرسمية المجانية ويقوم بالتنقل الذكي بين النماذج لضمان إجابات حية وسريعة وخالية تمامًا من أي انقطاع.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = selectedModel == SettingsRepository.MODEL_FLASH_LITE_LATEST,
                                onClick = { viewModel.saveSelectedModel(SettingsRepository.MODEL_FLASH_LITE_LATEST) },
                                label = { Text("gemini-flash-lite-latest (الموصى به - فائق السرعة وفوري)") },
                                modifier = Modifier.testTag("chip_model_flash_lite_latest")
                            )

                            FilterChip(
                                selected = selectedModel == SettingsRepository.MODEL_FLASH_3,
                                onClick = { viewModel.saveSelectedModel(SettingsRepository.MODEL_FLASH_3) },
                                label = { Text("gemini-3-flash-preview (نموذج التفكير المتقدم)") },
                                modifier = Modifier.testTag("chip_model_flash_3")
                            )

                            FilterChip(
                                selected = selectedModel == SettingsRepository.MODEL_FLASH_35,
                                onClick = { viewModel.saveSelectedModel(SettingsRepository.MODEL_FLASH_35) },
                                label = { Text("gemini-3.5-flash (النموذج الذكي الرسمي)") },
                                modifier = Modifier.testTag("chip_model_flash_35")
                            )
                        }
                    }
                }
            }

            // 3. AdMob Monetization Settings
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AdsClick, contentDescription = null, tint = DbGoldAccent)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "إعلانات Google AdMob",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Switch(
                                checked = adsEnabled,
                                onCheckedChange = { viewModel.toggleAds(it) },
                                modifier = Modifier.testTag("switch_ads_enabled")
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "مكان مخصص لمعرّف إعلان البانر لتحقيق الأرباح. يمكنك استخدام المعرّف الاختباري أثناء التجربة أو استبداله بمعرّفك الرسمي المعتمد.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = bannerInput,
                            onValueChange = { bannerInput = it },
                            label = { Text("معرّف وحدة إعلان البانر (Ad Unit ID)") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("input_admob_banner"),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.saveAdmobBannerId(bannerInput)
                                    Toast.makeText(context, "تم حفظ إعدادات الإعلانات", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Text("تحديث معرّف الإعلان")
                            }

                            OutlinedButton(
                                onClick = {
                                    bannerInput = SettingsRepository.DEFAULT_BANNER_ID
                                    viewModel.saveAdmobBannerId(SettingsRepository.DEFAULT_BANNER_ID)
                                }
                            ) {
                                Text("المعرّف التجريبي")
                            }
                        }
                    }
                }
            }

            // 4. Data & Privacy
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "إدارة البيانات والخصوصية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedButton(
                            onClick = { viewModel.navigateTo(Screen.PrivacyPolicy) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_view_privacy")
                        ) {
                            Icon(Icons.Default.Policy, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("قراءة سياسة الخصوصية الكاملة")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_reset_all_data")
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مسح جميع سجلات المحادثات",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("تأكيد مسح كافة المحادثات") },
            text = { Text("هل أنت متأكد من رغبتك في حذف جميع رسائل المحادثات لجميع الشخصيات الخمس؟ لا يمكن التراجع عن هذا الإجراء.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllData()
                        showResetDialog = false
                    }
                ) {
                    Text("مسح الكل", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

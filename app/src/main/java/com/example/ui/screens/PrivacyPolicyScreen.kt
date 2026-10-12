package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.DragonBallTopBar
import com.example.ui.theme.DbOrangePrimary
import com.example.viewmodel.ChatViewModel

@Composable
fun PrivacyPolicyScreen(viewModel: ChatViewModel) {
    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        topBar = {
            DragonBallTopBar(
                title = "سياسة الخصوصية",
                subtitle = "حماية البيانات وسرية المحادثات",
                onBackClick = { viewModel.navigateBack() }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .testTag("privacy_policy_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Policy, contentDescription = null, tint = DbOrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "مقدمة والتزام بالخصوصية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "نحن في تطبيق Dragon Ball Messenger نولي خصوصيتكم اهتمامًا بالغًا. تهدف هذه السياسة إلى توضيح كيفية تعامل التطبيق مع بياناتكم ومحادثاتكم بصورة شفافة ومسؤولة وفق أعلى معايير الأمان.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "1. حفظ المحادثات والبيانات المحلية",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "يتم حفظ سجل المحادثات محليًا داخل قاعدة بيانات Room المشفرة على جهازك فقط. لا يتم رفع سجل محادثاتك إلى أي خادم تخزين خارجي خاص بنا، ويمكنك مسح كافة المحادثات في أي وقت بضغطة زر واحدة من شاشة الإعدادات.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "2. خدمة الذكاء الاصطناعي (Google Gemini API)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "لإجراء المحادثات التفاعلية، تُرسل نصوص الرسائل مباشرة عبر اتصال مشفر وآمن (HTTPS) إلى واجهة Google Gemini API لإنشاء الرد المطلوب وفق سياق الشخصية. تخضع هذه العملية لسياسة خصوصية Google Cloud وGoogle AI دون تخزين دائم لبياناتك الشخصية.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "3. الإعلانات (Google AdMob)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "يستخدم التطبيق خدمة Google AdMob لعرض إعلانات غير معطلة لتجربة الاستخدام. قد تجمع خدمات Google معرّف الإعلانات الخاص بجهازك وفق سياسات Google الإعلانية لتقديم إعلانات ملائمة. يمكنك تعطيل الإعلانات أو ضبطها من شاشة الإعدادات.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "4. الأمان والاحترام والمحتوى الديني",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "يحتوي التطبيق على فلاتر أمان صارمة تضمن الالتزام التام بالاحترام المتبادل ومنع أي ألفاظ مسيئة أو شتائم أو مساس بالذات الإلهية والمقدسات الدينية، للحفاظ على بيئة آمنة وملهمة لجميع المستخدمين.",
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = 24.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

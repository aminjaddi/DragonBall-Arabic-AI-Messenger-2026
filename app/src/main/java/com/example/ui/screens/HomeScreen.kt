package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.Character
import com.example.ui.components.AdBannerComponent
import com.example.ui.components.DragonBallTopBar
import com.example.ui.theme.DbGoldAccent
import com.example.ui.theme.DbNavyBackground
import com.example.ui.theme.DbOrangePrimary
import com.example.ui.theme.DbShenronGreen
import com.example.viewmodel.ChatViewModel
import com.example.viewmodel.Screen

import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.ui.components.CharacterImageDialog

@Composable
fun HomeScreen(viewModel: ChatViewModel) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var previewCharacter by remember { mutableStateOf<Character?>(null) }
    val admobBannerId by viewModel.settingsRepository.admobBannerId.collectAsState()
    val adsEnabled by viewModel.settingsRepository.adsEnabled.collectAsState()

    if (previewCharacter != null) {
        CharacterImageDialog(
            character = previewCharacter!!,
            onDismiss = { previewCharacter = null }
        )
    }

    Scaffold(
        topBar = {
            DragonBallTopBar(
                title = "Dragon Ball Messenger",
                subtitle = "مراسلة ذكية باللغة العربية الفصحى"
            )
        },
        bottomBar = {
            Column {
                AdBannerComponent(
                    adUnitId = admobBannerId,
                    isEnabled = adsEnabled
                )

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "المحادثات") },
                        label = { Text("المحادثات") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DbOrangePrimary,
                            selectedTextColor = DbOrangePrimary,
                            indicatorColor = DbOrangePrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_chats")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.Default.People, contentDescription = "الشخصيات") },
                        label = { Text("الشخصيات") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DbOrangePrimary,
                            selectedTextColor = DbOrangePrimary,
                            indicatorColor = DbOrangePrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_characters")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { viewModel.navigateTo(Screen.Settings) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "الإعدادات") },
                        label = { Text("الإعدادات") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DbOrangePrimary,
                            selectedTextColor = DbOrangePrimary,
                            indicatorColor = DbOrangePrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_settings")
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { viewModel.navigateTo(Screen.About) },
                        icon = { Icon(Icons.Default.Info, contentDescription = "حول التطبيق") },
                        label = { Text("حول التطبيق") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DbOrangePrimary,
                            selectedTextColor = DbOrangePrimary,
                            indicatorColor = DbOrangePrimary.copy(alpha = 0.15f)
                        ),
                        modifier = Modifier.testTag("tab_about")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> ActiveChatsList(
                    characters = viewModel.characters,
                    onOpenChat = { char -> viewModel.openChatWith(char) },
                    onOpenDetail = { char -> viewModel.openCharacterDetail(char) },
                    onAvatarClick = { char -> previewCharacter = char }
                )
                1 -> CharactersGallery(
                    characters = viewModel.characters,
                    onOpenChat = { char -> viewModel.openChatWith(char) },
                    onOpenDetail = { char -> viewModel.openCharacterDetail(char) },
                    onAvatarClick = { char -> previewCharacter = char }
                )
            }
        }
    }
}

@Composable
fun ActiveChatsList(
    characters: List<Character>,
    onOpenChat: (Character) -> Unit,
    onOpenDetail: (Character) -> Unit,
    onAvatarClick: (Character) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Hero Welcome Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_hero_banner"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    DbNavyBackground,
                                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, DbGoldAccent, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon_dragonball),
                                contentDescription = "كرة دراغون بول",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "مرحبًا بك في عالم دراغون بول!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "اختر إحدى الشخصيات وتحدث معها مباشرة بإيجاز باللغة العربية الفصحى.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "المحادثات المباشرة",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${characters.size} شخصيات متصلة",
                    style = MaterialTheme.typography.labelMedium,
                    color = DbShenronGreen
                )
            }
        }

        items(characters, key = { it.id }) { character ->
            ChatCharacterCard(
                character = character,
                onOpenChat = { onOpenChat(character) },
                onOpenDetail = { onOpenDetail(character) },
                onAvatarClick = { onAvatarClick(character) }
            )
        }
    }
}

@Composable
fun ChatCharacterCard(
    character: Character,
    onOpenChat: () -> Unit,
    onOpenDetail: () -> Unit,
    onAvatarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenChat() }
            .testTag("chat_card_${character.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Character Avatar with online indicator (Clickable for full image preview)
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .border(2.5.dp, character.accentColor, CircleShape)
                        .clickable { onAvatarClick() }
                        .testTag("avatar_${character.id}")
                ) {
                    Image(
                        painter = painterResource(id = character.avatarRes),
                        contentDescription = character.nameAr,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Online indicator badge
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(DbShenronGreen)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = character.nameAr,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = character.accentColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = character.titleAr,
                            style = MaterialTheme.typography.labelSmall,
                            color = character.accentColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = character.quoteAr,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "الحالة: ${character.statusTextAr}",
                    style = MaterialTheme.typography.labelSmall,
                    color = character.accentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun CharactersGallery(
    characters: List<Character>,
    onOpenChat: (Character) -> Unit,
    onOpenDetail: (Character) -> Unit,
    onAvatarClick: (Character) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(characters, key = { it.id }) { character ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("gallery_card_${character.id}"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header portrait & info
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Clickable Character Portrait with Zoom Badge
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .border(3.dp, character.accentColor, RoundedCornerShape(18.dp))
                                .clickable { onAvatarClick(character) }
                                .testTag("gallery_portrait_${character.id}"),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            Image(
                                painter = painterResource(id = character.avatarRes),
                                contentDescription = character.nameAr,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )

                            // Zoom indicator
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.Black.copy(alpha = 0.6f))
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.ZoomIn,
                                        contentDescription = null,
                                        tint = character.accentColor,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "تكبير",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = character.nameAr,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = character.titleAr,
                                style = MaterialTheme.typography.bodySmall,
                                color = character.accentColor,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = character.shortDescAr,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Quote bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(character.accentColor.copy(alpha = 0.1f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = character.quoteAr,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = character.accentColor
                        )
                    }

                    // Action buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onOpenChat(character) },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_chat_${character.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = character.accentColor),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("محادثة ذكية")
                        }

                        OutlinedButton(
                            onClick = { onOpenDetail(character) },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_detail_${character.id}")
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("الملف")
                        }

                        IconButton(
                            onClick = { onAvatarClick(character) },
                            modifier = Modifier
                                .size(40.dp)
                                .background(character.accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                                .testTag("btn_preview_image_${character.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ZoomIn,
                                contentDescription = "معاينة صورة ${character.nameAr}",
                                tint = character.accentColor,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

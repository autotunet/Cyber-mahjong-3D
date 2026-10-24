package com.example.ui.screens

import android.app.Activity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ads.AdMobBannerView
import com.example.ads.AdMobManager
import com.example.data.LocalizationManager
import com.example.model.ScenarioTheme
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.CustomizationDialog
import com.example.ui.components.DifficultyDialog
import com.example.ui.components.HelpDialog
import com.example.ui.components.LanguageDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.LightBackground
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightCyan
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import com.example.viewmodel.MahjongViewModel

@Composable
fun MainMenuScreen(
    viewModel: MahjongViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val lang = uiState.language

    val isDark = uiState.isDarkMode
    val primaryAccent = if (isDark) CyberCyan else LightCyan
    val secondaryAccent = if (isDark) CyberMagenta else Color(0xFFC026D3)
    val goldAccent = if (isDark) CyberGold else Color(0xFFD97706)
    val cardBackground = if (isDark) CyberSurface.copy(alpha = 0.88f) else Color.White.copy(alpha = 0.94f)
    val cardBorder = if (isDark) CyberBorder else LightBorder
    val textPrimary = if (isDark) Color.White else LightTextPrimary
    val textSecondary = if (isDark) Color.White.copy(alpha = 0.7f) else LightTextSecondary

    // Atmospheric neon pulsing for title
    val infiniteTransition = rememberInfiniteTransition(label = "title_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "neon_glow"
    )

    val hasActiveGame = uiState.movesCount > 0 && uiState.tiles.any { !it.isMatched }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (isDark) CyberBackground else LightBackground)
    ) {
        // Atmospheric Background Wallpaper
        val bgDrawableId = when (uiState.selectedScenario) {
            ScenarioTheme.NEO_TOKYO -> context.resources.getIdentifier("bg_neo_tokyo", "drawable", context.packageName)
            ScenarioTheme.ORBITAL_STATION -> context.resources.getIdentifier("bg_orbital_station", "drawable", context.packageName)
            ScenarioTheme.CYBER_TEMPLE -> context.resources.getIdentifier("bg_neo_tokyo", "drawable", context.packageName)
            ScenarioTheme.QUANTUM_VOID -> context.resources.getIdentifier("bg_orbital_station", "drawable", context.packageName)
        }

        if (bgDrawableId != 0) {
            Image(
                painter = painterResource(id = bgDrawableId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = if (isDark) 0.38f else 0.14f
            )
        }

        // Overlay tint
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (isDark) Color(uiState.selectedScenario.atmosphericColorHex).copy(alpha = 0.75f)
                    else Color(0xFFF8FAFC).copy(alpha = 0.88f)
                )
        )

        // Main Content Scrollable Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ==================== TOP BAR: PROFILE & QUICK CONTROLS ====================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Level & Nanocores Pill
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(cardBackground)
                        .border(1.dp, cardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(primaryAccent.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${uiState.playerLevel}",
                            color = primaryAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = "${LocalizationManager.string("stat_level", lang)} ${uiState.playerLevel}",
                            color = textPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "⚡ ${uiState.nanocores}",
                                color = goldAccent,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Quick Controls: Language, Sound, Theme
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Language Selector Button
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = cardBackground,
                        border = BorderStroke(1.dp, cardBorder),
                        modifier = Modifier
                            .clickable { viewModel.setLanguageDialog(true) }
                            .testTag("btn_menu_language")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(text = lang.flag, fontSize = 14.sp)
                            Text(
                                text = lang.code.uppercase(),
                                color = primaryAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Sound Mute Toggle
                    val isSoundActive = uiState.isMusicEnabled || uiState.isSfxEnabled
                    IconButton(
                        onClick = { viewModel.toggleMasterAudio() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(cardBackground)
                            .border(1.dp, cardBorder, CircleShape)
                            .testTag("btn_menu_sound")
                    ) {
                        Icon(
                            imageVector = if (isSoundActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isSoundActive) "Silenciar" else "Activar Sonido",
                            tint = if (isSoundActive) primaryAccent else Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Persistent Settings Menu Button
                    IconButton(
                        onClick = { viewModel.setSettingsDialog(true) },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(cardBackground)
                            .border(1.dp, cardBorder, CircleShape)
                            .testTag("btn_menu_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Configuración",
                            tint = primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Theme Toggle
                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(cardBackground)
                            .border(1.dp, cardBorder, CircleShape)
                            .testTag("btn_menu_theme")
                    ) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = "Cambiar tema",
                            tint = if (isDark) primaryAccent else Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Scrollable Menu Body
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                // ==================== HERO APP TITLE ====================
                Text(
                    text = LocalizationManager.string("game_title", lang),
                    color = primaryAccent.copy(alpha = glowAlpha),
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 3.sp,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = LocalizationManager.string("game_subtitle", lang),
                    color = secondaryAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 2.dp, bottom = 20.dp)
                )

                // ==================== STATS OVERVIEW CARD ====================
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBackground)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = LocalizationManager.string("stat_high_score", lang),
                            color = textSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.score.coerceAtLeast(uiState.nanocores * 5)}",
                            color = primaryAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(cardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = LocalizationManager.string("stat_games_won", lang),
                            color = textSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${uiState.playerLevel - 1}",
                            color = goldAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(28.dp)
                            .background(cardBorder)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = LocalizationManager.string("btn_difficulty", lang),
                            color = textSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = uiState.difficulty.label,
                            color = secondaryAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ==================== PRIMARY PLAY BUTTON ====================
                Button(
                    onClick = {
                        if (activity != null && !hasActiveGame) {
                            AdMobManager.showAutomaticInterstitial(activity) {
                                viewModel.navigateToGame()
                            }
                        } else {
                            viewModel.navigateToGame()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("btn_menu_play"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryAccent
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (hasActiveGame) Icons.Default.Refresh else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (hasActiveGame) LocalizationManager.string("btn_continue", lang)
                            else LocalizationManager.string("btn_play", lang),
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // ==================== SECONDARY ACTION BUTTONS ====================

                // Select Difficulty
                OutlinedButton(
                    onClick = { viewModel.setDialog(difficulty = true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_menu_difficulty"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, secondaryAccent),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = secondaryAccent.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = secondaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizationManager.string("btn_difficulty", lang),
                            color = if (isDark) Color.White else textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Customization / Skins & Audio
                OutlinedButton(
                    onClick = { viewModel.setDialog(cosmetics = true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_menu_customization"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, cardBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = cardBackground
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizationManager.string("btn_customization", lang),
                            color = textPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Settings & Audio Menu Button
                OutlinedButton(
                    onClick = { viewModel.setSettingsDialog(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_menu_settings_main"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, primaryAccent.copy(alpha = 0.8f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = primaryAccent.copy(alpha = 0.08f)
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = primaryAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = LocalizationManager.string("btn_settings", lang),
                            color = primaryAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Achievements & Rules Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.setDialog(achievements = true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_menu_achievements"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.6f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = cardBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = goldAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LocalizationManager.string("btn_achievements", lang),
                            color = goldAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = { viewModel.setDialog(help = true) },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_menu_rules"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, cardBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = cardBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = textSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LocalizationManager.string("btn_how_to_play", lang),
                            color = textSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Direct Language Selector Button
                OutlinedButton(
                    onClick = { viewModel.setLanguageDialog(true) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_menu_open_languages"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, primaryAccent.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = primaryAccent.copy(alpha = 0.05f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = null,
                        tint = primaryAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${LocalizationManager.string("btn_language", lang)}: ${lang.flag} ${lang.nativeName}",
                        color = textPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // ==================== REAL ADMOB BANNER DEBAJO ====================
            AdMobBannerView(
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ==================== DIALOGS ====================

        if (uiState.showLanguageDialog) {
            LanguageDialog(
                currentLanguage = uiState.language,
                onSelectLanguage = { viewModel.setLanguage(it) },
                onDismiss = { viewModel.setLanguageDialog(false) }
            )
        }

        if (uiState.showDifficultyDialog) {
            DifficultyDialog(
                currentDifficulty = uiState.difficulty,
                language = uiState.language,
                onSelectDifficulty = { diff ->
                    if (activity != null) {
                        AdMobManager.showAutomaticInterstitial(activity) {
                            viewModel.startNewGame(diff)
                            viewModel.navigateToGame()
                        }
                    } else {
                        viewModel.startNewGame(diff)
                        viewModel.navigateToGame()
                    }
                },
                onDismiss = { viewModel.setDialog(difficulty = false) }
            )
        }

        if (uiState.showCosmeticsDialog) {
            CustomizationDialog(
                nanocores = uiState.nanocores,
                selectedSkin = uiState.selectedSkin,
                selectedScenario = uiState.selectedScenario,
                selectedQuality = uiState.graphicsQuality,
                unlockedSkins = uiState.unlockedSkins,
                unlockedScenarios = uiState.unlockedScenarios,
                isMusicEnabled = uiState.isMusicEnabled,
                isSfxEnabled = uiState.isSfxEnabled,
                isHapticsEnabled = uiState.isHapticsEnabled,
                isSpatialAudioEnabled = uiState.isSpatialAudioEnabled,
                musicVolume = uiState.musicVolume,
                sfxVolume = uiState.sfxVolume,
                ambientSoundMode = uiState.ambientSoundMode,
                onSelectSkin = { viewModel.purchaseOrSelectSkin(it) },
                onSelectScenario = { viewModel.purchaseOrSelectScenario(it) },
                onSelectQuality = { viewModel.setQuality(it) },
                onToggleMasterAudio = { viewModel.toggleMasterAudio() },
                onToggleMusic = { viewModel.toggleMusic() },
                onToggleSfx = { viewModel.toggleSfx() },
                onToggleHaptics = { viewModel.toggleHaptics() },
                onToggleSpatialAudio = { viewModel.toggleSpatialAudio() },
                onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                onSfxVolumeChange = { viewModel.setSfxVolume(it) },
                onSelectAmbientMode = { viewModel.setAmbientSoundMode(it) },
                language = uiState.language,
                onOpenLanguage = { viewModel.setLanguageDialog(true) },
                onDismiss = { viewModel.setDialog(cosmetics = false) }
            )
        }

        if (uiState.showAchievementsDialog) {
            AchievementsDialog(
                achievements = uiState.achievements,
                playerLevel = uiState.playerLevel,
                nanocores = uiState.nanocores,
                onDismiss = { viewModel.setDialog(achievements = false) },
                onShareAchievement = { ach -> viewModel.shareAchievement(ach, context) }
            )
        }

        if (uiState.showHelpDialog) {
            HelpDialog(
                language = uiState.language,
                onDismiss = { viewModel.setDialog(help = false) }
            )
        }

        if (uiState.showSettingsDialog) {
            SettingsDialog(
                isMusicEnabled = uiState.isMusicEnabled,
                isSfxEnabled = uiState.isSfxEnabled,
                isHapticsEnabled = uiState.isHapticsEnabled,
                isSpatialAudioEnabled = uiState.isSpatialAudioEnabled,
                musicVolume = uiState.musicVolume,
                sfxVolume = uiState.sfxVolume,
                ambientSoundMode = uiState.ambientSoundMode,
                isDarkMode = uiState.isDarkMode,
                selectedQuality = uiState.graphicsQuality,
                language = uiState.language,
                onToggleMasterAudio = { viewModel.toggleMasterAudio() },
                onToggleMusic = { viewModel.toggleMusic() },
                onToggleSfx = { viewModel.toggleSfx() },
                onToggleHaptics = { viewModel.toggleHaptics() },
                onToggleSpatialAudio = { viewModel.toggleSpatialAudio() },
                onMusicVolumeChange = { viewModel.setMusicVolume(it) },
                onSfxVolumeChange = { viewModel.setSfxVolume(it) },
                onAmbientSoundModeChange = { viewModel.setAmbientSoundMode(it) },
                onToggleTheme = { viewModel.toggleTheme() },
                onSelectQuality = { viewModel.setQuality(it) },
                onPlayTestSound = { viewModel.playTestSound() },
                onOpenLanguage = { viewModel.setLanguageDialog(true) },
                onDismiss = { viewModel.setSettingsDialog(false) }
            )
        }
    }
}

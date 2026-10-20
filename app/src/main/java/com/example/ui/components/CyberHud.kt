package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import com.example.data.LocalizationManager
import com.example.model.AppLanguage
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Achievement
import com.example.model.Difficulty
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightCyan
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary

@Composable
fun CyberHud(
    score: Int,
    combo: Int,
    playerLevel: Int,
    playerXp: Int,
    nanocores: Int,
    remainingTiles: Int,
    availablePairs: Int,
    elapsedSeconds: Int,
    remainingTimeSeconds: Int?,
    totalTimeLimitSeconds: Int = 180,
    lastTimeBonus: Int? = null,
    difficulty: Difficulty,
    latestAchievement: Achievement?,
    isDarkMode: Boolean = true,
    isMusicEnabled: Boolean = true,
    isSfxEnabled: Boolean = true,
    language: AppLanguage = AppLanguage.ES,
    onBackToMenu: () -> Unit = {},
    onOpenLanguage: () -> Unit = {},
    onToggleSoundMute: () -> Unit = {},
    onToggleTheme: () -> Unit,
    onDismissAchievement: () -> Unit,
    onOpenCosmetics: () -> Unit,
    onOpenAchievements: () -> Unit,
    onOpenDifficulty: () -> Unit,
    onOpenHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hudSurface = if (isDarkMode) CyberSurface.copy(alpha = 0.92f) else Color.White.copy(alpha = 0.95f)
    val hudBorder = if (isDarkMode) CyberBorder else LightBorder
    val hudTextPrimary = if (isDarkMode) Color.White else LightTextPrimary
    val hudTextSecondary = if (isDarkMode) Color.White.copy(alpha = 0.7f) else LightTextSecondary
    val badgeSurface = if (isDarkMode) CyberSurfaceVariant.copy(alpha = 0.85f) else Color(0xFFF1F5F9).copy(alpha = 0.90f)

    val primaryAccent = if (isDarkMode) CyberCyan else LightCyan
    val goldAccent = if (isDarkMode) CyberGold else Color(0xFFD97706)
    val amberAccent = if (isDarkMode) CyberAmber else Color(0xFFB45309)

    // Pressure calculation
    val currentRemaining = remainingTimeSeconds ?: 120
    val timeRatio = (currentRemaining.toFloat() / totalTimeLimitSeconds.coerceAtLeast(1)).coerceIn(0f, 1f)
    val isCritical = currentRemaining <= 25
    val isCaution = currentRemaining in 26..50

    // Pulsing effect for critical pressure
    val infiniteTransition = rememberInfiniteTransition(label = "pressure_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400),
            repeatMode = RepeatMode.Reverse
        ),
        label = "critical_pulse"
    )

    val pressureColor = when {
        isCritical -> Color(0xFFEF4444).copy(alpha = if (isCritical) pulseAlpha else 1f)
        isCaution -> CyberGold
        else -> primaryAccent
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        // Achievement unlock toast banner
        AnimatedVisibility(
            visible = latestAchievement != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            latestAchievement?.let { ach ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(CyberGold.copy(alpha = 0.95f), CyberMagenta.copy(alpha = 0.95f))
                            )
                        )
                        .clickable { onDismissAchievement() }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🏆", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "¡LOGRO DESBLOQUEADO!",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = ach.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "+${ach.rewardNanocores} ⚡",
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // ==================== 1. TOP HEADER BAR ====================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(hudSurface)
                .border(1.2.dp, hudBorder, RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Back to Home + Player Level & XP
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackToMenu,
                    modifier = Modifier.size(34.dp).testTag("btn_hud_back_to_menu")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = LocalizationManager.string("btn_back_to_menu", language),
                        tint = primaryAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(2.dp))

                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(primaryAccent.copy(alpha = 0.16f))
                        .border(1.5.dp, primaryAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L$playerLevel",
                        color = primaryAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isDarkMode) "HACKER" else "MAESTRO",
                            color = hudTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "⚡ $nanocores",
                            color = goldAccent,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    val currentLevelXp = playerXp % 500
                    LinearProgressIndicator(
                        progress = { currentLevelXp / 500f },
                        modifier = Modifier
                            .width(68.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = primaryAccent,
                        trackColor = if (isDarkMode) CyberBorder else Color(0xFFE2E8F0)
                    )
                }
            }

            // Quick Menu Actions: Language, Theme, Sound, Dialogs
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Language Selector Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) CyberSurfaceVariant else Color(0xFFF1F5F9))
                        .border(
                            1.dp,
                            if (isDarkMode) primaryAccent.copy(alpha = 0.5f) else Color(0xFFD97706).copy(alpha = 0.5f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onOpenLanguage() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .testTag("btn_hud_language"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = language.flag, fontSize = 12.sp)
                }
                // Interactive Light/Dark Theme Switch
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isDarkMode) CyberSurfaceVariant else Color(0xFFF1F5F9))
                        .border(
                            1.dp,
                            if (isDarkMode) primaryAccent.copy(alpha = 0.6f) else Color(0xFFD97706).copy(alpha = 0.6f),
                            RoundedCornerShape(16.dp)
                        )
                        .clickable { onToggleTheme() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("switch_theme_mode"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = if (isDarkMode) "Modo Claro" else "Modo Oscuro",
                            tint = if (isDarkMode) CyberCyan else Color(0xFFD97706),
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = if (isDarkMode) "Cyber" else "Claro",
                            color = if (isDarkMode) CyberCyan else Color(0xFFD97706),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Quick Sound Mute / Unmute Toggle Button
                val isAudioActive = isMusicEnabled || isSfxEnabled
                IconButton(
                    onClick = onToggleSoundMute,
                    modifier = Modifier.size(34.dp).testTag("btn_sound_toggle")
                ) {
                    Icon(
                        imageVector = if (isAudioActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = if (isAudioActive) "Silenciar Sonido" else "Activar Sonido",
                        tint = if (isAudioActive) primaryAccent else Color(0xFFEF4444),
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onOpenDifficulty,
                    modifier = Modifier.size(34.dp).testTag("btn_difficulty")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Dificultad",
                        tint = primaryAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onOpenCosmetics,
                    modifier = Modifier.size(34.dp).testTag("btn_cosmetics")
                ) {
                    Icon(
                        imageVector = Icons.Default.Palette,
                        contentDescription = "Personalizar",
                        tint = if (isDarkMode) CyberMagenta else Color(0xFFC026D3),
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onOpenAchievements,
                    modifier = Modifier.size(34.dp).testTag("btn_achievements")
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Logros",
                        tint = goldAccent,
                        modifier = Modifier.size(19.dp)
                    )
                }

                IconButton(
                    onClick = onOpenHelp,
                    modifier = Modifier.size(34.dp).testTag("btn_help")
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Ayuda",
                        tint = hudTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ==================== 2. SECONDARY METRICS BAR (Score, Combo, Tiles) ====================
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Score & Combo Streak
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeSurface)
                    .border(1.dp, hudBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$score ${LocalizationManager.string("score", language)}",
                    color = hudTextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                if (combo > 1) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isDarkMode) CyberMagenta else Color(0xFFC026D3))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "x$combo",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Tiles Remaining & Available Pairs
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(badgeSurface)
                    .border(1.dp, hudBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${LocalizationManager.string("tiles_remaining", language)}: $remainingTiles",
                    color = primaryAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${LocalizationManager.string("pairs_available", language)}: $availablePairs",
                    color = if (availablePairs > 0) amberAccent else Color(0xFFDC2626),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // ==================== 3. DEDICATED CYBER CHRONOMETER & PRESSURE GAUGE ====================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (isCritical) {
                        Color(0xFF380814).copy(alpha = if (isDarkMode) 0.88f else 0.95f)
                    } else {
                        badgeSurface
                    }
                )
                .border(
                    width = if (isCritical) 1.8.dp else 1.2.dp,
                    color = pressureColor,
                    shape = RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("cyber_chronometer_widget")
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chronometer Clock Display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Cronómetro",
                            tint = pressureColor,
                            modifier = Modifier.size(18.dp)
                        )

                        val m = currentRemaining / 60
                        val s = currentRemaining % 60
                        val clockFormatted = String.format("%02d:%02d", m, s)

                        Text(
                            text = clockFormatted,
                            color = pressureColor,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.testTag("chronometer_digits")
                        )

                        // Floating Time Bonus Badge (+Xs)
                        AnimatedVisibility(
                            visible = lastTimeBonus != null,
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut() + scaleOut()
                        ) {
                            lastTimeBonus?.let { bonus ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF10B981))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "+${bonus}s",
                                        color = Color.Black,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }

                    // Pressure & Challenge Status Indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val statusText = when {
                            isCritical -> "¡PELIGRO TIEMPO!"
                            isCaution -> "PRESIÓN ACTIVA"
                            else -> "RITMO VELOZ ⚡"
                        }
                        Text(
                            text = statusText,
                            color = pressureColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )

                        val elM = elapsedSeconds / 60
                        val elS = elapsedSeconds % 60
                        Text(
                            text = "(${String.format("%02d:%02d", elM, elS)})",
                            color = hudTextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // High-Tech Depleting Pressure Gauge Bar
                LinearProgressIndicator(
                    progress = { timeRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = pressureColor,
                    trackColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFCBD5E1)
                )
            }
        }
    }
}

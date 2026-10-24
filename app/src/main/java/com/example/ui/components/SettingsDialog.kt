package com.example.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.AmbientSoundMode
import com.example.data.LocalizationManager
import com.example.model.AppLanguage
import com.example.model.GraphicsQuality
import com.example.ui.theme.CyberAmber
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightCyan
import com.example.ui.theme.LightTextPrimary
import com.example.ui.theme.LightTextSecondary
import kotlin.math.roundToInt

@Composable
fun SettingsDialog(
    isMusicEnabled: Boolean,
    isSfxEnabled: Boolean,
    isHapticsEnabled: Boolean,
    isSpatialAudioEnabled: Boolean,
    musicVolume: Float,
    sfxVolume: Float,
    ambientSoundMode: AmbientSoundMode,
    isDarkMode: Boolean,
    selectedQuality: GraphicsQuality,
    language: AppLanguage,
    onToggleMasterAudio: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleSfx: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleSpatialAudio: () -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onSfxVolumeChange: (Float) -> Unit,
    onAmbientSoundModeChange: (AmbientSoundMode) -> Unit,
    onToggleTheme: () -> Unit,
    onSelectQuality: (GraphicsQuality) -> Unit,
    onPlayTestSound: () -> Unit,
    onOpenLanguage: () -> Unit,
    onDismiss: () -> Unit
) {
    val isDark = isDarkMode
    val surfaceBg = if (isDark) CyberSurface else Color(0xFFF8FAFC)
    val cardBg = if (isDark) CyberSurfaceVariant.copy(alpha = 0.8f) else Color.White
    val borderCol = if (isDark) CyberBorder else LightBorder
    val primaryAccent = if (isDark) CyberCyan else LightCyan
    val secondaryAccent = if (isDark) CyberMagenta else Color(0xFFC026D3)
    val textPrimary = if (isDark) Color.White else LightTextPrimary
    val textSecondary = if (isDark) Color.White.copy(alpha = 0.7f) else LightTextSecondary

    val isMasterAudioOn = isMusicEnabled || isSfxEnabled

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, primaryAccent, RoundedCornerShape(24.dp)),
            color = surfaceBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // ==================== HEADER ====================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(primaryAccent.copy(alpha = 0.18f))
                                .border(1.dp, primaryAccent.copy(alpha = 0.5f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = primaryAccent,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Column {
                            Text(
                                text = LocalizationManager.string("settings_title", language),
                                color = primaryAccent,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = LocalizationManager.string("settings_subtitle", language),
                                color = textSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("btn_close_settings_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar",
                            tint = textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==================== SETTINGS CONTENT ====================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ----- 1. MASTER AUDIO SWITCH CARD -----
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isMasterAudioOn) primaryAccent.copy(alpha = 0.12f)
                                    else Color(0xFFEF4444).copy(alpha = 0.10f)
                                )
                                .border(
                                    1.2.dp,
                                    if (isMasterAudioOn) primaryAccent else Color(0xFFEF4444).copy(alpha = 0.5f),
                                    RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isMasterAudioOn) primaryAccent.copy(alpha = 0.2f)
                                            else Color(0xFFEF4444).copy(alpha = 0.2f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isMasterAudioOn) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                                        contentDescription = null,
                                        tint = if (isMasterAudioOn) primaryAccent else Color(0xFFEF4444),
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = LocalizationManager.string("settings_master_audio", language),
                                        color = textPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (isMasterAudioOn) LocalizationManager.string("sound_active", language)
                                        else LocalizationManager.string("sound_muted", language),
                                        color = if (isMasterAudioOn) CyberGreen else Color(0xFFEF4444),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Switch(
                                checked = isMasterAudioOn,
                                onCheckedChange = { onToggleMasterAudio() },
                                modifier = Modifier.testTag("switch_master_audio"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.Black,
                                    checkedTrackColor = primaryAccent,
                                    uncheckedThumbColor = Color.White.copy(alpha = 0.6f),
                                    uncheckedTrackColor = Color.DarkGray
                                )
                            )
                        }
                    }

                    // ----- 2. BACKGROUND AMBIENT MUSIC CARD -----
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBg)
                                .border(1.dp, borderCol, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MusicNote,
                                        contentDescription = null,
                                        tint = if (isMusicEnabled) primaryAccent else textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = LocalizationManager.string("settings_bg_music", language),
                                            color = textPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = LocalizationManager.string("settings_bg_music_sub", language),
                                            color = textSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isMusicEnabled,
                                    onCheckedChange = { onToggleMusic() },
                                    modifier = Modifier.testTag("switch_music_toggle"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = primaryAccent,
                                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                        uncheckedTrackColor = Color.DarkGray
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Music Volume Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Volumen de Música",
                                    color = if (isMusicEnabled) textPrimary else textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isMusicEnabled) "${(musicVolume * 100).roundToInt()}%" else "Silenciado",
                                    color = if (isMusicEnabled) primaryAccent else textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Slider(
                                value = musicVolume,
                                onValueChange = onMusicVolumeChange,
                                enabled = isMusicEnabled,
                                valueRange = 0f..1f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("slider_music_volume"),
                                colors = SliderDefaults.colors(
                                    thumbColor = primaryAccent,
                                    activeTrackColor = primaryAccent,
                                    inactiveTrackColor = primaryAccent.copy(alpha = 0.2f),
                                    disabledThumbColor = Color.Gray,
                                    disabledActiveTrackColor = Color.DarkGray
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Ambient Sound Mode Chips
                            Text(
                                text = LocalizationManager.string("settings_ambient_track", language),
                                color = textSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                AmbientSoundMode.values().forEach { mode ->
                                    val isSelected = ambientSoundMode == mode && isMusicEnabled
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (isSelected) primaryAccent.copy(alpha = 0.18f)
                                                else Color.Black.copy(alpha = 0.25f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) primaryAccent else borderCol,
                                                RoundedCornerShape(10.dp)
                                            )
                                            .clickable(enabled = isMusicEnabled) {
                                                onAmbientSoundModeChange(mode)
                                            }
                                            .padding(horizontal = 10.dp, vertical = 7.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = mode.title,
                                                color = if (isSelected) primaryAccent else textPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = mode.description,
                                                color = textSecondary,
                                                fontSize = 9.sp,
                                                maxLines = 1
                                            )
                                        }

                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = null,
                                                tint = primaryAccent,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // ----- 3. SOUND EFFECTS (SFX) CARD -----
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBg)
                                .border(1.dp, borderCol, RoundedCornerShape(16.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.GraphicEq,
                                        contentDescription = null,
                                        tint = if (isSfxEnabled) secondaryAccent else textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = LocalizationManager.string("settings_sfx", language),
                                            color = textPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = LocalizationManager.string("settings_sfx_sub", language),
                                            color = textSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isSfxEnabled,
                                    onCheckedChange = { onToggleSfx() },
                                    modifier = Modifier.testTag("switch_sfx_toggle"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = secondaryAccent,
                                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                        uncheckedTrackColor = Color.DarkGray
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // SFX Volume Slider
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Volumen de Efectos",
                                    color = if (isSfxEnabled) textPrimary else textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (isSfxEnabled) "${(sfxVolume * 100).roundToInt()}%" else "Silenciado",
                                    color = if (isSfxEnabled) secondaryAccent else textSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Slider(
                                value = sfxVolume,
                                onValueChange = onSfxVolumeChange,
                                enabled = isSfxEnabled,
                                valueRange = 0f..1f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("slider_sfx_volume"),
                                colors = SliderDefaults.colors(
                                    thumbColor = secondaryAccent,
                                    activeTrackColor = secondaryAccent,
                                    inactiveTrackColor = secondaryAccent.copy(alpha = 0.2f),
                                    disabledThumbColor = Color.Gray,
                                    disabledActiveTrackColor = Color.DarkGray
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Test SFX Button
                            OutlinedButton(
                                onClick = onPlayTestSound,
                                enabled = isSfxEnabled,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("btn_test_sfx"),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    if (isSfxEnabled) secondaryAccent else Color.Gray.copy(alpha = 0.3f)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (isSfxEnabled) secondaryAccent else textSecondary
                                )
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = LocalizationManager.string("settings_test_sound", language),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // ----- 4. IMMERSION & HAPTICS CARD -----
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBg)
                                .border(1.dp, borderCol, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // 3D Spatial Audio
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Headphones,
                                        contentDescription = null,
                                        tint = if (isSpatialAudioEnabled) primaryAccent else textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = LocalizationManager.string("settings_spatial_audio", language),
                                            color = textPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = LocalizationManager.string("settings_spatial_audio_sub", language),
                                            color = textSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isSpatialAudioEnabled,
                                    onCheckedChange = { onToggleSpatialAudio() },
                                    modifier = Modifier.testTag("switch_spatial_audio"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = primaryAccent,
                                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                        uncheckedTrackColor = Color.DarkGray
                                    )
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(borderCol))

                            // Haptic Feedback
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Vibration,
                                        contentDescription = null,
                                        tint = if (isHapticsEnabled) CyberGold else textSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = LocalizationManager.string("settings_haptics", language),
                                            color = textPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = LocalizationManager.string("settings_haptics_sub", language),
                                            color = textSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isHapticsEnabled,
                                    onCheckedChange = { onToggleHaptics() },
                                    modifier = Modifier.testTag("switch_haptics"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = CyberGold,
                                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                        uncheckedTrackColor = Color.DarkGray
                                    )
                                )
                            }
                        }
                    }

                    // ----- 5. THEME & GRAPHICS QUALITY CARD -----
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(cardBg)
                                .border(1.dp, borderCol, RoundedCornerShape(16.dp))
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Dark / Light Theme Toggle
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                        contentDescription = null,
                                        tint = if (isDark) primaryAccent else Color(0xFFD97706),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Column {
                                        Text(
                                            text = LocalizationManager.string("settings_theme", language),
                                            color = textPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isDark) "Modo Cyberpunk Oscuro" else "Modo Zen Claro",
                                            color = textSecondary,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Switch(
                                    checked = isDark,
                                    onCheckedChange = { onToggleTheme() },
                                    modifier = Modifier.testTag("switch_settings_theme"),
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = primaryAccent,
                                        uncheckedThumbColor = Color.White.copy(alpha = 0.5f),
                                        uncheckedTrackColor = Color(0xFFD97706)
                                    )
                                )
                            }

                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(borderCol))

                            // Graphics Quality Selector
                            Text(
                                text = LocalizationManager.string("settings_graphics_quality", language),
                                color = textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                GraphicsQuality.values().forEach { quality ->
                                    val isQSelected = selectedQuality == quality
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (isQSelected) primaryAccent.copy(alpha = 0.2f)
                                                else Color.Black.copy(alpha = 0.2f)
                                            )
                                            .border(
                                                1.dp,
                                                if (isQSelected) primaryAccent else borderCol,
                                                RoundedCornerShape(8.dp)
                                            )
                                            .clickable { onSelectQuality(quality) }
                                            .padding(vertical = 6.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = quality.label,
                                            color = if (isQSelected) primaryAccent else textSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = if (isQSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // ----- 6. LANGUAGE DIRECT SHORTCUT -----
                    item {
                        OutlinedButton(
                            onClick = onOpenLanguage,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_settings_open_language"),
                            shape = RoundedCornerShape(12.dp),
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
                                text = "${LocalizationManager.string("btn_language", language)}: ${language.flag} ${language.nativeName}",
                                color = textPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ==================== CLOSE / SAVE BUTTON ====================
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("btn_save_close_settings"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryAccent)
                ) {
                    Text(
                        text = LocalizationManager.string("btn_close", language),
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

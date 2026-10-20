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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wallpaper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.AppLanguage
import com.example.model.GraphicsQuality
import com.example.model.ScenarioTheme
import com.example.model.TileSkin
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import com.example.ui.theme.CyberSurface

@Composable
fun CustomizationDialog(
    nanocores: Int,
    selectedSkin: TileSkin,
    selectedScenario: ScenarioTheme,
    selectedQuality: GraphicsQuality,
    unlockedSkins: Set<String>,
    unlockedScenarios: Set<String>,
    isMusicEnabled: Boolean,
    isSfxEnabled: Boolean,
    isHapticsEnabled: Boolean,
    isSpatialAudioEnabled: Boolean,
    musicVolume: Float,
    sfxVolume: Float,
    ambientSoundMode: AmbientSoundMode,
    language: AppLanguage = AppLanguage.ES,
    onSelectSkin: (TileSkin) -> Unit,
    onSelectScenario: (ScenarioTheme) -> Unit,
    onSelectQuality: (GraphicsQuality) -> Unit,
    onToggleMasterAudio: () -> Unit = {},
    onToggleMusic: () -> Unit,
    onToggleSfx: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleSpatialAudio: () -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onSfxVolumeChange: (Float) -> Unit,
    onSelectAmbientMode: (AmbientSoundMode) -> Unit,
    onOpenLanguage: () -> Unit = {},
    onDismiss: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Fichas", "Escenarios", "Audio & Calidad")

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, CyberMagenta, RoundedCornerShape(24.dp)),
            color = CyberSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header with Nanocores balance and Language chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ARSENAL Y AUDIO",
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "⚡ $nanocores Nanocores disponibles",
                            color = CyberGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.45f),
                            border = BorderStroke(1.dp, CyberCyan.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .clickable { onOpenLanguage() }
                                .padding(end = 4.dp)
                                .testTag("btn_cosmetics_language")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = language.flag, fontSize = 12.sp)
                                Text(
                                    text = language.code.uppercase(),
                                    color = CyberCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cerrar",
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Tab Bar
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = CyberCyan,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CyberMagenta
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) CyberCyan else Color.Gray
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Content per tab
                Box(modifier = Modifier.height(350.dp)) {
                    when (selectedTab) {
                        0 -> TileSkinsList(
                            nanocores = nanocores,
                            selectedSkin = selectedSkin,
                            unlockedSkins = unlockedSkins,
                            onSelectSkin = onSelectSkin
                        )
                        1 -> ScenariosList(
                            nanocores = nanocores,
                            selectedScenario = selectedScenario,
                            unlockedScenarios = unlockedScenarios,
                            onSelectScenario = onSelectScenario
                        )
                        2 -> AudioAndQualitySettings(
                            selectedQuality = selectedQuality,
                            isMusicEnabled = isMusicEnabled,
                            isSfxEnabled = isSfxEnabled,
                            isHapticsEnabled = isHapticsEnabled,
                            isSpatialAudioEnabled = isSpatialAudioEnabled,
                            musicVolume = musicVolume,
                            sfxVolume = sfxVolume,
                            ambientSoundMode = ambientSoundMode,
                            onSelectQuality = onSelectQuality,
                            onToggleMasterAudio = onToggleMasterAudio,
                            onToggleMusic = onToggleMusic,
                            onToggleSfx = onToggleSfx,
                            onToggleHaptics = onToggleHaptics,
                            onToggleSpatialAudio = onToggleSpatialAudio,
                            onMusicVolumeChange = onMusicVolumeChange,
                            onSfxVolumeChange = onSfxVolumeChange,
                            onSelectAmbientMode = onSelectAmbientMode
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_close_cosmetics"),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberMagenta)
                ) {
                    Text(
                        text = "LISTO",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TileSkinsList(
    nanocores: Int,
    selectedSkin: TileSkin,
    unlockedSkins: Set<String>,
    onSelectSkin: (TileSkin) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(TileSkin.values()) { skin ->
            val isUnlocked = unlockedSkins.contains(skin.id)
            val isSelected = selectedSkin == skin

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) CyberCyan.copy(alpha = 0.12f)
                        else Color.Black.copy(alpha = 0.35f)
                    )
                    .border(
                        1.2.dp,
                        if (isSelected) CyberCyan else CyberBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectSkin(skin) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Two-Tone Swatch Preview
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(skin.bodyColorHex))
                            .border(1.5.dp, Color(skin.primaryAccentHex), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "中",
                            color = Color(skin.secondaryAccentHex),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = skin.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = skin.description,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Action chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                isSelected -> CyberCyan
                                isUnlocked -> CyberGreen.copy(alpha = 0.25f)
                                nanocores >= skin.unlockCost -> CyberGold.copy(alpha = 0.25f)
                                else -> Color.DarkGray.copy(alpha = 0.4f)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when {
                            isSelected -> "ACTIVO"
                            isUnlocked -> "ELEGIR"
                            else -> "⚡ ${skin.unlockCost}"
                        },
                        color = when {
                            isSelected -> Color.Black
                            isUnlocked -> CyberGreen
                            nanocores >= skin.unlockCost -> CyberGold
                            else -> Color.Gray
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenariosList(
    nanocores: Int,
    selectedScenario: ScenarioTheme,
    unlockedScenarios: Set<String>,
    onSelectScenario: (ScenarioTheme) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        items(ScenarioTheme.values()) { sc ->
            val isUnlocked = unlockedScenarios.contains(sc.id)
            val isSelected = selectedScenario == sc

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isSelected) CyberMagenta.copy(alpha = 0.12f)
                        else Color.Black.copy(alpha = 0.35f)
                    )
                    .border(
                        1.2.dp,
                        if (isSelected) CyberMagenta else CyberBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelectScenario(sc) }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(sc.gridColorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallpaper,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = sc.title,
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = sc.subtitle,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            when {
                                isSelected -> CyberMagenta
                                isUnlocked -> CyberGreen.copy(alpha = 0.25f)
                                nanocores >= sc.unlockCost -> CyberGold.copy(alpha = 0.25f)
                                else -> Color.DarkGray.copy(alpha = 0.4f)
                            }
                        )
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = when {
                            isSelected -> "ACTIVO"
                            isUnlocked -> "ELEGIR"
                            else -> "⚡ ${sc.unlockCost}"
                        },
                        color = when {
                            isSelected -> Color.White
                            isUnlocked -> CyberGreen
                            nanocores >= sc.unlockCost -> CyberGold
                            else -> Color.Gray
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun AudioAndQualitySettings(
    selectedQuality: GraphicsQuality,
    isMusicEnabled: Boolean,
    isSfxEnabled: Boolean,
    isHapticsEnabled: Boolean,
    isSpatialAudioEnabled: Boolean,
    musicVolume: Float,
    sfxVolume: Float,
    ambientSoundMode: AmbientSoundMode,
    onSelectQuality: (GraphicsQuality) -> Unit,
    onToggleMasterAudio: () -> Unit,
    onToggleMusic: () -> Unit,
    onToggleSfx: () -> Unit,
    onToggleHaptics: () -> Unit,
    onToggleSpatialAudio: () -> Unit,
    onMusicVolumeChange: (Float) -> Unit,
    onSfxVolumeChange: (Float) -> Unit,
    onSelectAmbientMode: (AmbientSoundMode) -> Unit
) {
    val isAnyAudioActive = isMusicEnabled || isSfxEnabled

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Section: Master Audio Control
        item {
            Text(
                text = "CONTROL DE AUDIO PRINCIPAL",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Master Mute / Unmute Toggle Card
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isAnyAudioActive) CyberCyan.copy(alpha = 0.12f)
                        else Color.Red.copy(alpha = 0.12f)
                    )
                    .border(
                        1.5.dp,
                        if (isAnyAudioActive) CyberCyan else Color(0xFFEF4444),
                        RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (isAnyAudioActive) CyberCyan.copy(alpha = 0.22f)
                                else Color(0xFFEF4444).copy(alpha = 0.22f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isAnyAudioActive) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = if (isAnyAudioActive) "Audio Activo" else "Audio Silenciado",
                            tint = if (isAnyAudioActive) CyberCyan else Color(0xFFEF4444),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (isAnyAudioActive) "Sonido General: ACTIVO" else "Sonido General: SILENCIADO",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isAnyAudioActive) "Música y efectos audibles" else "Música y efectos silenciados",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }

                Switch(
                    checked = isAnyAudioActive,
                    onCheckedChange = { onToggleMasterAudio() },
                    modifier = Modifier.testTag("switch_master_mute"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberCyan,
                        checkedTrackColor = CyberCyan.copy(alpha = 0.45f),
                        uncheckedThumbColor = Color(0xFFEF4444),
                        uncheckedTrackColor = Color(0xFFEF4444).copy(alpha = 0.3f)
                    )
                )
            }
        }

        // Section: Individual Sound Controls
        item {
            Text(
                text = "AJUSTES INDIVIDUALES DE MÚSICA & EFECTOS",
                color = CyberGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Background Music Toggle & Slider Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.38f))
                    .border(
                        1.dp,
                        if (isMusicEnabled) CyberCyan.copy(alpha = 0.6f) else CyberBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isMusicEnabled) Icons.Default.MusicNote else Icons.Default.VolumeOff,
                            contentDescription = null,
                            tint = if (isMusicEnabled) CyberCyan else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Música de Fondo Zen",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isMusicEnabled) "Banda sonora sintetizada activa" else "Silenciado",
                                color = if (isMusicEnabled) CyberCyan else Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Switch(
                        checked = isMusicEnabled,
                        onCheckedChange = { onToggleMusic() },
                        modifier = Modifier.testTag("switch_music_toggle"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberCyan,
                            checkedTrackColor = CyberCyan.copy(alpha = 0.4f)
                        )
                    )
                }

                if (isMusicEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Volumen Música", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                        Text(text = "${(musicVolume * 100).toInt()}%", color = CyberCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = musicVolume,
                        onValueChange = onMusicVolumeChange,
                        modifier = Modifier.testTag("slider_music_volume"),
                        colors = SliderDefaults.colors(
                            thumbColor = CyberCyan,
                            activeTrackColor = CyberCyan,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }
            }
        }

        // Sound Effects (SFX) Toggle & Slider Card
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.38f))
                    .border(
                        1.dp,
                        if (isSfxEnabled) CyberMagenta.copy(alpha = 0.6f) else CyberBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = if (isSfxEnabled) Icons.Default.VolumeDown else Icons.Default.VolumeOff,
                            contentDescription = null,
                            tint = if (isSfxEnabled) CyberMagenta else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Efectos de Sonido (SFX)",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isSfxEnabled) "Toques, emparejamiento y combos" else "Silenciado",
                                color = if (isSfxEnabled) CyberMagenta else Color.Gray,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Switch(
                        checked = isSfxEnabled,
                        onCheckedChange = { onToggleSfx() },
                        modifier = Modifier.testTag("switch_sfx_toggle"),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = CyberMagenta,
                            checkedTrackColor = CyberMagenta.copy(alpha = 0.4f)
                        )
                    )
                }

                if (isSfxEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Volumen Efectos", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                        Text(text = "${(sfxVolume * 100).toInt()}%", color = CyberMagenta, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = sfxVolume,
                        onValueChange = onSfxVolumeChange,
                        modifier = Modifier.testTag("slider_sfx_volume"),
                        colors = SliderDefaults.colors(
                            thumbColor = CyberMagenta,
                            activeTrackColor = CyberMagenta,
                            inactiveTrackColor = Color.DarkGray
                        )
                    )
                }
            }
        }

        // Section: Spatial Stereo Audio & Modes
        item {
            Text(
                text = "AUDIO ESPACIAL & BANDA SONORA",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Spatial Audio Switch
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, if (isSpatialAudioEnabled) CyberCyan else CyberBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Headphones,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Audio Espacial Estéreo",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Paneo estéreo según la posición de cada ficha en el tablero",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 10.sp
                        )
                    }
                }
                Switch(
                    checked = isSpatialAudioEnabled,
                    onCheckedChange = { onToggleSpatialAudio() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberCyan,
                        checkedTrackColor = CyberCyan.copy(alpha = 0.4f)
                    )
                )
            }
        }

        // Ambient Track Modes Selector
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(10.dp)
            ) {
                Text(
                    text = "Banda Sonora Relajante",
                    color = CyberGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                AmbientSoundMode.values().forEach { mode ->
                    val isSelected = ambientSoundMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) CyberGold.copy(alpha = 0.18f) else Color.Transparent)
                            .border(1.dp, if (isSelected) CyberGold else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { onSelectAmbientMode(mode) }
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(mode.title, color = if (isSelected) CyberGold else Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(mode.description, color = Color.White.copy(alpha = 0.6f), fontSize = 10.sp)
                        }
                        if (isSelected) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = CyberGold, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Haptics switch
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.35f))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = CyberGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Vibración Háptica al pulsar",
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
                Switch(
                    checked = isHapticsEnabled,
                    onCheckedChange = { onToggleHaptics() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = CyberGold,
                        checkedTrackColor = CyberGold.copy(alpha = 0.4f)
                    )
                )
            }
        }

        // Section: Graphics Quality
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "RENDIMIENTO Y OPTIMIZACIÓN",
                color = CyberCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(GraphicsQuality.values()) { q ->
            val isSelected = selectedQuality == q
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) CyberCyan.copy(alpha = 0.15f)
                        else Color.Black.copy(alpha = 0.35f)
                    )
                    .border(
                        1.dp,
                        if (isSelected) CyberCyan else CyberBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelectQuality(q) }
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = q.title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = q.description,
                        color = Color.White.copy(alpha = 0.65f),
                        fontSize = 10.sp
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import com.example.ads.AdMobBannerView
import com.example.ads.AdMobManager
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ScenarioTheme
import com.example.ui.components.AchievementsDialog
import com.example.ui.components.CyberControlsBar
import com.example.ui.components.CyberHud
import com.example.ui.components.CustomizationDialog
import com.example.ui.components.DifficultyDialog
import com.example.ui.components.GameOverDialog
import com.example.ui.components.HelpDialog
import com.example.ui.components.LanguageDialog
import com.example.ui.effects.CyberMatchEffectOverlay
import com.example.ui.render2d.Mahjong2DRenderer
import com.example.ui.render2d.ProjectedTile2D
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.LightBackground
import com.example.viewmodel.MahjongViewModel
import kotlinx.coroutines.delay

@Composable
fun MahjongGameScreen(
    viewModel: MahjongViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity
    val textMeasurer = rememberTextMeasurer()

    // Handle Android system back gesture to smoothly return to Main Menu with ad transition
    BackHandler {
        if (activity != null) {
            AdMobManager.showAutomaticInterstitial(activity) {
                viewModel.navigateToMainMenu()
            }
        } else {
            viewModel.navigateToMainMenu()
        }
    }

    var projectedTilesCache by remember { mutableStateOf<List<ProjectedTile2D>>(emptyList()) }
    var matchTile1Pos by remember { mutableStateOf<Offset?>(null) }
    var matchTile2Pos by remember { mutableStateOf<Offset?>(null) }

    // Auto-dismiss reshuffle notice
    LaunchedEffect(uiState.matrixReshuffleNotice) {
        if (uiState.matrixReshuffleNotice != null) {
            delay(2800)
            viewModel.dismissReshuffleNotice()
        }
    }

    // Auto-dismiss time bonus indicator
    LaunchedEffect(uiState.lastTimeBonusGranted) {
        if (uiState.lastTimeBonusGranted != null) {
            delay(1600)
            viewModel.dismissTimeBonus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (uiState.isDarkMode) CyberBackground else LightBackground)
    ) {
        // Atmospheric Background Wallpaper
        val bgDrawableId = when (uiState.selectedScenario) {
            ScenarioTheme.NEO_TOKYO -> {
                context.resources.getIdentifier("bg_neo_tokyo", "drawable", context.packageName)
            }
            ScenarioTheme.ORBITAL_STATION -> {
                context.resources.getIdentifier("bg_orbital_station", "drawable", context.packageName)
            }
            ScenarioTheme.CYBER_TEMPLE -> {
                context.resources.getIdentifier("bg_neo_tokyo", "drawable", context.packageName)
            }
            ScenarioTheme.QUANTUM_VOID -> {
                context.resources.getIdentifier("bg_orbital_station", "drawable", context.packageName)
            }
        }

        if (bgDrawableId != 0) {
            Image(
                painter = painterResource(id = bgDrawableId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = if (uiState.isDarkMode) 0.38f else 0.12f
            )
        }

        // Overlay tint for cyber ambiance or clean light visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (uiState.isDarkMode) Color(uiState.selectedScenario.atmosphericColorHex)
                    else Color(0xFFF8FAFC).copy(alpha = 0.85f)
                )
        )

        // ==================== 2D MAHJONG BOARD ====================
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoomFactor, _ ->
                        if (zoomFactor != 1f) {
                            viewModel.updateCameraZoom(zoomFactor)
                        }
                        if (pan != Offset.Zero) {
                            viewModel.updateCameraPan(pan.x, pan.y)
                        }
                    }
                }
                .pointerInput(projectedTilesCache) {
                    detectTapGestures { offset ->
                        val clicked = Mahjong2DRenderer.findTileAtScreenPoint2D(offset, projectedTilesCache)
                        if (clicked != null) {
                            val selected = uiState.selectedTile
                            if (selected != null && selected.id != clicked.id && selected.identity.matches(clicked.identity)) {
                                // Match detected: record screen positions for connecting laser beam and cybernetic glow particles
                                val pt1 = projectedTilesCache.firstOrNull { it.tile.id == selected.id }
                                val pt2 = projectedTilesCache.firstOrNull { it.tile.id == clicked.id }
                                if (pt1 != null && pt2 != null) {
                                    matchTile1Pos = Offset(pt1.screenX, pt1.screenY)
                                    matchTile2Pos = Offset(pt2.screenX, pt2.screenY)
                                }
                            }
                            viewModel.onTileClicked(clicked)
                        }
                    }
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val responsiveTileWidth = (size.width / 15f).coerceIn(52f, 110f)
            val responsiveTileHeight = responsiveTileWidth * 1.32f

            // 1. 2D Cyber Grid Floor
            Mahjong2DRenderer.draw2DGridFloor(
                drawScope = this,
                center = center,
                pan = Offset(uiState.panX, uiState.panY),
                zoom = uiState.zoom,
                scenario = uiState.selectedScenario,
                isDarkMode = uiState.isDarkMode
            )

            // 2. Calculate 2D Tile Positions (Immediately excludes eliminated tiles!)
            val projected = Mahjong2DRenderer.layoutTiles2D(
                tiles = uiState.tiles,
                center = center,
                zoom = uiState.zoom,
                pan = Offset(uiState.panX, uiState.panY),
                baseTileWidth = responsiveTileWidth,
                baseTileHeight = responsiveTileHeight
            )
            projectedTilesCache = projected

            // Ensure match positions are computed even if gesture cache was missed
            uiState.lastMatchEvent?.let { ev ->
                if (matchTile1Pos == null) {
                    matchTile1Pos = Mahjong2DRenderer.computeTileScreenCenter(
                        ev.gridX1, ev.gridY1, ev.gridZ1, center, uiState.zoom, Offset(uiState.panX, uiState.panY),
                        responsiveTileWidth, responsiveTileHeight
                    )
                }
                if (matchTile2Pos == null) {
                    matchTile2Pos = Mahjong2DRenderer.computeTileScreenCenter(
                        ev.gridX2, ev.gridY2, ev.gridZ2, center, uiState.zoom, Offset(uiState.panX, uiState.panY),
                        responsiveTileWidth, responsiveTileHeight
                    )
                }
            }

            // 3. Render 2D Active Tiles with Authentic Mahjong Art
            for (pt in projected) {
                Mahjong2DRenderer.drawTile2D(
                    drawScope = this,
                    projected = pt,
                    skin = uiState.selectedSkin,
                    quality = uiState.graphicsQuality,
                    textMeasurer = textMeasurer,
                    isDarkMode = uiState.isDarkMode
                )
            }
        }

        // ==================== CYBER MATCH PARTICLES & GLOW EFFECT ====================
        CyberMatchEffectOverlay(
            matchEvent = uiState.lastMatchEvent,
            tile1Center = matchTile1Pos,
            tile2Center = matchTile2Pos,
            quality = uiState.graphicsQuality,
            textMeasurer = textMeasurer
        )

        // ==================== MATRIX REORGANIZED NOTIFICATION TOAST ====================
        AnimatedVisibility(
            visible = uiState.matrixReshuffleNotice != null,
            enter = fadeIn() + slideInVertically(initialOffsetY = { -50 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { -50 }),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 70.dp)
        ) {
            uiState.matrixReshuffleNotice?.let { notice ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF03101C).copy(alpha = 0.94f),
                    border = BorderStroke(1.5.dp, CyberCyan),
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = notice,
                            color = CyberCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // ==================== TOP HUD ====================
        CyberHud(
            score = uiState.score,
            combo = uiState.combo,
            playerLevel = uiState.playerLevel,
            playerXp = uiState.playerXp,
            nanocores = uiState.nanocores,
            remainingTiles = uiState.tiles.count { !it.isMatched },
            availablePairs = uiState.availablePairsCount,
            elapsedSeconds = uiState.elapsedSeconds,
            remainingTimeSeconds = uiState.remainingTimeSeconds,
            totalTimeLimitSeconds = uiState.totalTimeLimitSeconds,
            lastTimeBonus = uiState.lastTimeBonusGranted,
            difficulty = uiState.difficulty,
            latestAchievement = uiState.latestUnlockedAchievement,
            isDarkMode = uiState.isDarkMode,
            isMusicEnabled = uiState.isMusicEnabled,
            isSfxEnabled = uiState.isSfxEnabled,
            language = uiState.language,
            onBackToMenu = {
                if (activity != null) {
                    AdMobManager.showAutomaticInterstitial(activity) {
                        viewModel.navigateToMainMenu()
                    }
                } else {
                    viewModel.navigateToMainMenu()
                }
            },
            onOpenLanguage = { viewModel.setLanguageDialog(true) },
            onToggleSoundMute = { viewModel.toggleMasterAudio() },
            onToggleTheme = { viewModel.toggleTheme() },
            onDismissAchievement = { viewModel.dismissAchievementToast() },
            onOpenCosmetics = { viewModel.setDialog(cosmetics = true) },
            onOpenAchievements = { viewModel.setDialog(achievements = true) },
            onOpenDifficulty = { viewModel.setDialog(difficulty = true) },
            onOpenHelp = { viewModel.setDialog(help = true) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )

        // ==================== BOTTOM CONTROLS & ADMOB BANNER DEBAJO ====================
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            CyberControlsBar(
                availablePairs = uiState.availablePairsCount,
                isDarkMode = uiState.isDarkMode,
                onHint = { viewModel.requestHint() },
                onShuffle = { viewModel.shuffleRemaining() },
                onUndo = { viewModel.undoLastMove() },
                onResetView = { viewModel.resetView() },
                onZoomIn = { viewModel.zoomIn() },
                onZoomOut = { viewModel.zoomOut() },
                modifier = Modifier.fillMaxWidth()
            )

            // Real AdMob Banner Ad debajo (ID 1)
            AdMobBannerView(
                modifier = Modifier.fillMaxWidth()
            )
        }

        // ==================== DIALOGS ====================

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

        if (uiState.showDifficultyDialog) {
            DifficultyDialog(
                currentDifficulty = uiState.difficulty,
                language = uiState.language,
                onSelectDifficulty = { diff ->
                    if (activity != null) {
                        AdMobManager.showAutomaticInterstitial(activity) {
                            viewModel.startNewGame(diff)
                        }
                    } else {
                        viewModel.startNewGame(diff)
                    }
                },
                onDismiss = { viewModel.setDialog(difficulty = false) }
            )
        }

        if (uiState.showHelpDialog) {
            HelpDialog(
                language = uiState.language,
                onDismiss = { viewModel.setDialog(help = false) }
            )
        }

        if (uiState.showLanguageDialog) {
            LanguageDialog(
                currentLanguage = uiState.language,
                onSelectLanguage = { viewModel.setLanguage(it) },
                onDismiss = { viewModel.setLanguageDialog(false) }
            )
        }

        if (uiState.isGameOver || uiState.isVictory) {
            GameOverDialog(
                isVictory = uiState.isVictory,
                score = uiState.score,
                movesCount = uiState.movesCount,
                elapsedSeconds = uiState.elapsedSeconds,
                difficulty = uiState.difficulty,
                playerLevel = uiState.playerLevel,
                language = uiState.language,
                onNextLevel = {
                    if (activity != null) {
                        AdMobManager.showAutomaticInterstitial(activity) {
                            viewModel.advanceToNextLevel()
                        }
                    } else {
                        viewModel.advanceToNextLevel()
                    }
                },
                onRestart = {
                    if (activity != null) {
                        AdMobManager.showAutomaticInterstitial(activity) {
                            viewModel.startNewGame(uiState.difficulty)
                        }
                    } else {
                        viewModel.startNewGame(uiState.difficulty)
                    }
                },
                onMainMenu = {
                    if (activity != null) {
                        AdMobManager.showAutomaticInterstitial(activity) {
                            viewModel.navigateToMainMenu()
                        }
                    } else {
                        viewModel.navigateToMainMenu()
                    }
                },
                onShare = { viewModel.shareVictory(context) }
            )
        }
    }
}

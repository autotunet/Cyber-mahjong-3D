package com.example.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.audio.AmbientSoundMode
import com.example.audio.CyberAudioEngine
import com.example.data.GamePreferencesRepository
import com.example.model.Achievement
import com.example.model.Difficulty
import com.example.model.GraphicsQuality
import com.example.model.MahjongLayout
import com.example.model.MahjongTile
import com.example.model.ScenarioTheme
import com.example.model.TileSkin
import com.example.model.AppLanguage
import com.example.model.AppScreen
import com.example.model.MatchEffectEvent
import com.example.sensor.OrientationSensorManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiState(
    val currentScreen: AppScreen = AppScreen.MAIN_MENU,
    val language: AppLanguage = AppLanguage.ES,
    val showLanguageDialog: Boolean = false,
    val showSettingsDialog: Boolean = false,
    val tiles: List<MahjongTile> = emptyList(),
    val selectedTile: MahjongTile? = null,
    val score: Int = 0,
    val combo: Int = 0,
    val movesCount: Int = 0,
    val elapsedSeconds: Int = 0,
    val remainingTimeSeconds: Int? = null,
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val isGameOver: Boolean = false,
    val isVictory: Boolean = false,
    val availablePairsCount: Int = 0,
    val lastMatchEvent: MatchEffectEvent? = null,

    // 2D Pan & Zoom Board View
    val zoom: Float = 1.0f,
    val panX: Float = 0f,
    val panY: Float = 0f,

    // Progression & Cosmetics
    val playerLevel: Int = 1,
    val playerXp: Int = 0,
    val nanocores: Int = 200,
    val selectedSkin: TileSkin = TileSkin.CYBERPUNK_NEON,
    val selectedScenario: ScenarioTheme = ScenarioTheme.NEO_TOKYO,
    val graphicsQuality: GraphicsQuality = GraphicsQuality.BALANCED,
    val unlockedSkins: Set<String> = setOf(TileSkin.CYBERPUNK_NEON.id),
    val unlockedScenarios: Set<String> = setOf(ScenarioTheme.NEO_TOKYO.id),
    val achievements: List<Achievement> = emptyList(),
    val latestUnlockedAchievement: Achievement? = null,

    // Sound & Spatial Audio Settings
    val isMusicEnabled: Boolean = true,
    val isSfxEnabled: Boolean = true,
    val isHapticsEnabled: Boolean = true,
    val isSpatialAudioEnabled: Boolean = true,
    val musicVolume: Float = 0.75f,
    val sfxVolume: Float = 0.85f,
    val ambientSoundMode: AmbientSoundMode = AmbientSoundMode.ZEN_DRONE,

    // Active Dialogs
    val showAchievementsDialog: Boolean = false,
    val showCosmeticsDialog: Boolean = false,
    val showDifficultyDialog: Boolean = false,
    val showHelpDialog: Boolean = false,

    // Theme (Light mode vs Cyber dark mode)
    val isDarkMode: Boolean = true,

    // Dynamic Level & Matrix Reshuffle Feedback
    val matrixReshuffleNotice: String? = null,

    // Match Chronometer & Pressure System
    val totalTimeLimitSeconds: Int = 180,
    val lastTimeBonusGranted: Int? = null
)

class MahjongViewModel(
    private val repo: GamePreferencesRepository,
    private val audioEngine: CyberAudioEngine,
    private val sensorManager: OrientationSensorManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val undoStack = mutableListOf<Pair<MahjongTile, MahjongTile>>()
    private var timerJob: Job? = null
    private var autoRotateJob: Job? = null
    private var gazeJob: Job? = null

    init {
        loadPersistedData()
        val initialDifficulty = getDifficultyForLevel(repo.level)
        startNewGame(initialDifficulty)
        audioEngine.setMusicEnabled(repo.isMusicEnabled, repo.selectedScenario.ambientDroneBaseFreq)
        audioEngine.setSfxEnabled(repo.isSfxEnabled)
        audioEngine.setHapticsEnabled(repo.isHapticsEnabled)
        audioEngine.setSpatialAudioEnabled(repo.isSpatialAudioEnabled)
        audioEngine.setMusicVolume(repo.musicVolume)
        audioEngine.setSfxVolume(repo.sfxVolume)
        val mode = AmbientSoundMode.values().firstOrNull { it.name == repo.ambientTrackMode } ?: AmbientSoundMode.ZEN_DRONE
        audioEngine.setAmbientMode(mode, repo.selectedScenario.ambientDroneBaseFreq)
    }

    private fun loadPersistedData() {
        val initialAchievements = listOf(
            Achievement("FIRST_LINK", "Primer Enlace", "Empareja tu primera pareja de fichas", 50, "link"),
            Achievement("COMBO_LINK", "Enlace Cuántico", "Alcanza un combo x3 consecutivo", 100, "bolt"),
            Achievement("COMBO_MASTER", "Racha Cuántica", "Alcanza un combo x5 en una misma partida", 150, "bolt"),
            Achievement("LAYER_HACKER", "Hacker de Capas", "Despeja fichas en el nivel superior Z=4", 120, "layers"),
            Achievement("ZEN_PURIST", "Zen Cósmico", "Gana una partida sin usar reorganización", 200, "zen"),
            Achievement("COSMETIC_LORD", "Coleccionista Cyber", "Desbloquea 2 skins de fichas", 250, "palette"),
            Achievement("GALAXY_EXPLORER", "Viajero Estelar", "Desbloquea 2 escenarios temáticos", 300, "globe"),
            Achievement("GRANDMASTER", "Hacker Legendario", "Completa el tablero en dificultad Hacker Neo", 500, "trophy")
        )

        val unlockedIds = repo.getUnlockedAchievements()
        val mappedAchievements = initialAchievements.map { ach ->
            ach.copy(isUnlocked = unlockedIds.contains(ach.id), progress = if (unlockedIds.contains(ach.id)) 1f else 0f)
        }

        val ambientMode = AmbientSoundMode.values().firstOrNull { it.name == repo.ambientTrackMode } ?: AmbientSoundMode.ZEN_DRONE

        val savedLang = AppLanguage.fromCode(repo.language)

        _uiState.update {
            it.copy(
                language = savedLang,
                playerLevel = repo.level,
                playerXp = repo.xp,
                nanocores = repo.nanocores,
                selectedSkin = repo.selectedSkin,
                selectedScenario = repo.selectedScenario,
                graphicsQuality = repo.graphicsQuality,
                unlockedSkins = repo.getUnlockedSkins(),
                unlockedScenarios = repo.getUnlockedScenarios(),
                achievements = mappedAchievements,
                isMusicEnabled = repo.isMusicEnabled,
                isSfxEnabled = repo.isSfxEnabled,
                isHapticsEnabled = repo.isHapticsEnabled,
                isSpatialAudioEnabled = repo.isSpatialAudioEnabled,
                musicVolume = repo.musicVolume,
                sfxVolume = repo.sfxVolume,
                ambientSoundMode = ambientMode,
                isDarkMode = repo.isDarkMode
            )
        }
    }

    fun getDifficultyForLevel(level: Int): Difficulty {
        return when {
            level <= 2 -> Difficulty.EASY
            level <= 4 -> Difficulty.MEDIUM
            level <= 6 -> Difficulty.HARD
            else -> Difficulty.EXPERT
        }
    }

    fun startNewGame(difficulty: Difficulty) {
        val newTiles = MahjongLayout.generateGameBoard(difficulty)
        undoStack.clear()

        val timeLimit = difficulty.timeLimitSeconds ?: 180

        _uiState.update {
            it.copy(
                tiles = newTiles,
                selectedTile = null,
                score = 0,
                combo = 0,
                movesCount = 0,
                elapsedSeconds = 0,
                totalTimeLimitSeconds = timeLimit,
                remainingTimeSeconds = timeLimit,
                lastTimeBonusGranted = null,
                difficulty = difficulty,
                isGameOver = false,
                isVictory = false,
                availablePairsCount = countAvailablePairs(newTiles),
                zoom = if (difficulty == Difficulty.HARD || difficulty == Difficulty.EXPERT) 0.88f else 1.0f,
                panX = 0f,
                panY = 0f,
                showDifficultyDialog = false
            )
        }

        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.update { state ->
                    if (state.isGameOver || state.isVictory) return@update state

                    val newElapsed = state.elapsedSeconds + 1
                    val newRemaining = state.remainingTimeSeconds?.let { (it - 1).coerceAtLeast(0) }
                    val isTimeout = newRemaining != null && newRemaining <= 0

                    if (newRemaining != null && newRemaining in 1..20) {
                        audioEngine.playTimerTick(isUrgent = newRemaining <= 8)
                    }

                    if (isTimeout) {
                        audioEngine.playError()
                        state.copy(
                            elapsedSeconds = newElapsed,
                            remainingTimeSeconds = 0,
                            isGameOver = true,
                            isVictory = false
                        )
                    } else {
                        state.copy(
                            elapsedSeconds = newElapsed,
                            remainingTimeSeconds = newRemaining
                        )
                    }
                }
            }
        }
    }

    private fun countAvailablePairs(tiles: List<MahjongTile>): Int {
        val playable = tiles.filter { MahjongLayout.isTilePlayable(it, tiles) }
        var pairs = 0
        for (i in playable.indices) {
            for (j in (i + 1) until playable.size) {
                if (playable[i].identity.matches(playable[j].identity)) {
                    pairs++
                }
            }
        }
        return pairs
    }

    private fun computeTilePan(tile: MahjongTile): Float {
        return (tile.gridX / 12f).coerceIn(-1f, 1f)
    }

    fun onTileClicked(tile: MahjongTile) {
        val state = _uiState.value
        if (state.isGameOver || state.isVictory) return

        val pan = computeTilePan(tile)

        if (!MahjongLayout.isTilePlayable(tile, state.tiles)) {
            audioEngine.playError()
            return
        }

        val currentSelected = state.selectedTile

        if (currentSelected == null) {
            // First tile selection with spatial panning
            audioEngine.playSpatialTileClick(pan, tile.gridZ.toFloat())
            _uiState.update { s ->
                s.copy(
                    selectedTile = tile,
                    tiles = s.tiles.map { t ->
                        if (t.id == tile.id) t.copy(isSelected = true, isHinted = false)
                        else t.copy(isSelected = false, isHinted = false)
                    }
                )
            }
        } else if (currentSelected.id == tile.id) {
            // Unselect same tile
            audioEngine.playSpatialTileClick(pan, tile.gridZ.toFloat())
            _uiState.update { s ->
                s.copy(
                    selectedTile = null,
                    tiles = s.tiles.map { if (it.id == tile.id) it.copy(isSelected = false) else it }
                )
            }
        } else {
            // Second tile selected - check match!
            if (currentSelected.identity.matches(tile.identity)) {
                // Match confirmed!
                handleTileMatch(currentSelected, tile)
            } else {
                // Mismatch: switch selection to the new playable tile
                audioEngine.playSpatialTileClick(pan, tile.gridZ.toFloat())
                _uiState.update { s ->
                    s.copy(
                        selectedTile = tile,
                        combo = 0, // Reset combo streak on mismatch
                        tiles = s.tiles.map { t ->
                            when (t.id) {
                                tile.id -> t.copy(isSelected = true, isHinted = false)
                                else -> t.copy(isSelected = false, isHinted = false)
                            }
                        }
                    )
                }
            }
        }
    }

    private fun handleTileMatch(tile1: MahjongTile, tile2: MahjongTile) {
        val newCombo = _uiState.value.combo + 1
        val points = (100 * newCombo * _uiState.value.difficulty.scoreMultiplier).toInt()
        val earnedNanocores = 5 * newCombo

        val pan1 = computeTilePan(tile1)
        val pan2 = computeTilePan(tile2)
        audioEngine.playSpatialTileMatch(pan1, pan2, newCombo)
        undoStack.add(Pair(tile1, tile2))

        val matchEvent = MatchEffectEvent(
            eventId = System.currentTimeMillis(),
            tile1Id = tile1.id,
            tile2Id = tile2.id,
            gridX1 = tile1.gridX,
            gridY1 = tile1.gridY,
            gridZ1 = tile1.gridZ,
            gridX2 = tile2.gridX,
            gridY2 = tile2.gridY,
            gridZ2 = tile2.gridZ,
            points = points,
            combo = newCombo
        )

        val updatedTiles = _uiState.value.tiles.map { t ->
            if (t.id == tile1.id || t.id == tile2.id) {
                t.copy(isMatched = true, isSelected = false, isHinted = false)
            } else {
                t.copy(isHinted = false)
            }
        }

        val remainingActiveCount = updatedTiles.count { !it.isMatched }
        val isVictory = remainingActiveCount == 0

        var finalTiles = updatedTiles
        var availablePairs = MahjongLayout.countAvailablePairs(finalTiles)
        var autoShuffleNotice: String? = null

        // GUARANTEED MOVES: If tiles remain but 0 pairs are playable, automatically reorganize!
        if (!isVictory && remainingActiveCount > 0 && availablePairs == 0) {
            finalTiles = MahjongLayout.guaranteedReshuffle(finalTiles)
            availablePairs = MahjongLayout.countAvailablePairs(finalTiles)
            audioEngine.playShuffle()
            autoShuffleNotice = "⚡ Matriz Reorganizada: ¡$availablePairs movimientos listos!"
        }

        // Time Bonus on Match: Quick combos award valuable extra seconds!
        val timeBonus = 3 + (newCombo * 2)
        val currentRemaining = _uiState.value.remainingTimeSeconds
        val totalLimit = _uiState.value.totalTimeLimitSeconds
        val updatedRemaining = currentRemaining?.let { (it + timeBonus).coerceAtMost(totalLimit) }

        var totalEarnedPoints = points
        if (isVictory) {
            audioEngine.playVictory()
            repo.gamesWon += 1
            val timeBonusScore = ((updatedRemaining ?: 0) * 12 * _uiState.value.difficulty.scoreMultiplier).toInt()
            totalEarnedPoints += 500 + timeBonusScore
            repo.addRewards(totalEarnedPoints / 2, 100)
            if (_uiState.value.difficulty == Difficulty.HARD || _uiState.value.difficulty == Difficulty.EXPERT) {
                unlockAchievement("GRANDMASTER")
            }
            if (undoStack.size == _uiState.value.tiles.size / 2) {
                unlockAchievement("ZEN_PURIST")
            }
        } else {
            repo.addRewards(points / 2, earnedNanocores)
        }
        repo.tilesMatched += 2

        // Achievements check
        unlockAchievement("FIRST_LINK")
        if (newCombo >= 3) {
            unlockAchievement("COMBO_LINK")
        }
        if (newCombo >= 5) {
            unlockAchievement("COMBO_MASTER")
        }
        if (tile1.gridZ >= 4 || tile2.gridZ >= 4) {
            unlockAchievement("LAYER_HACKER")
        }

        _uiState.update { s ->
            s.copy(
                tiles = finalTiles,
                selectedTile = null,
                score = s.score + totalEarnedPoints,
                combo = newCombo,
                movesCount = s.movesCount + 1,
                remainingTimeSeconds = updatedRemaining ?: s.remainingTimeSeconds,
                lastTimeBonusGranted = timeBonus,
                availablePairsCount = availablePairs,
                isVictory = isVictory,
                isGameOver = false, // Never stuck: moves are always guaranteed!
                playerXp = repo.xp,
                playerLevel = repo.level,
                nanocores = repo.nanocores,
                lastMatchEvent = matchEvent,
                matrixReshuffleNotice = autoShuffleNotice ?: s.matrixReshuffleNotice
            )
        }
    }

    fun requestHint() {
        val match = MahjongLayout.findAvailableMatch(_uiState.value.tiles)
        if (match != null) {
            audioEngine.playTileClick()
            _uiState.update { s ->
                s.copy(
                    tiles = s.tiles.map { t ->
                        if (t.id == match.first.id || t.id == match.second.id) {
                            t.copy(isHinted = true)
                        } else {
                            t.copy(isHinted = false)
                        }
                    }
                )
            }
        } else {
            audioEngine.playError()
        }
    }

    fun shuffleRemaining() {
        audioEngine.playShuffle()
        val currentTiles = _uiState.value.tiles
        val reshuffled = MahjongLayout.guaranteedReshuffle(currentTiles)
        val pairs = MahjongLayout.countAvailablePairs(reshuffled)

        _uiState.update { s ->
            s.copy(
                tiles = reshuffled,
                selectedTile = null,
                availablePairsCount = pairs,
                matrixReshuffleNotice = "⚡ Mezclado: ¡$pairs movimientos disponibles!"
            )
        }
    }

    fun advanceToNextLevel() {
        val nextLevel = repo.level + 1
        repo.level = nextLevel
        repo.addRewards(350, 60)
        val scaledDifficulty = getDifficultyForLevel(nextLevel)
        startNewGame(scaledDifficulty)
    }

    fun dismissReshuffleNotice() {
        _uiState.update { it.copy(matrixReshuffleNotice = null) }
    }

    fun dismissTimeBonus() {
        _uiState.update { it.copy(lastTimeBonusGranted = null) }
    }

    fun undoLastMove() {
        if (undoStack.isEmpty()) {
            audioEngine.playError()
            return
        }
        audioEngine.playTileClick()
        val (t1, t2) = undoStack.removeAt(undoStack.lastIndex)

        val restored = _uiState.value.tiles.map { t ->
            if (t.id == t1.id || t.id == t2.id) {
                t.copy(isMatched = false, isSelected = false, isHinted = false)
            } else {
                t
            }
        }

        _uiState.update { s ->
            s.copy(
                tiles = restored,
                selectedTile = null,
                combo = 0,
                movesCount = (s.movesCount - 1).coerceAtLeast(0),
                availablePairsCount = countAvailablePairs(restored)
            )
        }
    }

    fun updateCameraZoom(factor: Float) {
        _uiState.update { s ->
            s.copy(zoom = (s.zoom * factor).coerceIn(0.50f, 2.5f))
        }
    }

    fun updateCameraPan(dx: Float, dy: Float) {
        _uiState.update { s ->
            s.copy(
                panX = (s.panX + dx).coerceIn(-600f, 600f),
                panY = (s.panY + dy).coerceIn(-600f, 600f)
            )
        }
    }

    fun zoomIn() {
        audioEngine.playTileClick()
        updateCameraZoom(1.18f)
    }

    fun zoomOut() {
        audioEngine.playTileClick()
        updateCameraZoom(0.85f)
    }

    fun resetView() {
        audioEngine.playTileClick()
        _uiState.update { s ->
            val defaultZoom = if (s.difficulty == Difficulty.HARD || s.difficulty == Difficulty.EXPERT) 0.82f else 0.95f
            s.copy(
                panX = 0f,
                panY = 0f,
                zoom = defaultZoom
            )
        }
    }

    fun resetCamera() = resetView()

    fun toggleTheme() {
        audioEngine.playTileClick()
        val newMode = !_uiState.value.isDarkMode
        repo.isDarkMode = newMode
        _uiState.update { it.copy(isDarkMode = newMode) }
    }

    fun setDarkMode(dark: Boolean) {
        repo.isDarkMode = dark
        _uiState.update { it.copy(isDarkMode = dark) }
    }

    fun purchaseOrSelectSkin(skin: TileSkin) {
        val unlocked = _uiState.value.unlockedSkins
        if (unlocked.contains(skin.id)) {
            // Already owned, select it
            audioEngine.playTileClick()
            repo.selectedSkin = skin
            _uiState.update { it.copy(selectedSkin = skin) }
        } else {
            // Purchase
            if (_uiState.value.nanocores >= skin.unlockCost) {
                audioEngine.playVictory()
                repo.nanocores -= skin.unlockCost
                repo.unlockSkin(skin.id)
                repo.selectedSkin = skin
                val newUnlocked = repo.getUnlockedSkins()
                _uiState.update {
                    it.copy(
                        selectedSkin = skin,
                        nanocores = repo.nanocores,
                        unlockedSkins = newUnlocked
                    )
                }
                if (newUnlocked.size >= 2) {
                    unlockAchievement("COSMETIC_LORD")
                }
            } else {
                audioEngine.playError()
            }
        }
    }

    fun purchaseOrSelectScenario(scenario: ScenarioTheme) {
        val unlocked = _uiState.value.unlockedScenarios
        if (unlocked.contains(scenario.id)) {
            audioEngine.playTileClick()
            repo.selectedScenario = scenario
            audioEngine.startAmbientDrone(scenario.ambientDroneBaseFreq)
            _uiState.update { it.copy(selectedScenario = scenario) }
        } else {
            if (_uiState.value.nanocores >= scenario.unlockCost) {
                audioEngine.playVictory()
                repo.nanocores -= scenario.unlockCost
                repo.unlockScenario(scenario.id)
                repo.selectedScenario = scenario
                audioEngine.startAmbientDrone(scenario.ambientDroneBaseFreq)
                val newUnlocked = repo.getUnlockedScenarios()
                _uiState.update {
                    it.copy(
                        selectedScenario = scenario,
                        nanocores = repo.nanocores,
                        unlockedScenarios = newUnlocked
                    )
                }
                if (newUnlocked.size >= 2) {
                    unlockAchievement("GALAXY_EXPLORER")
                }
            } else {
                audioEngine.playError()
            }
        }
    }

    fun setQuality(quality: GraphicsQuality) {
        repo.graphicsQuality = quality
        _uiState.update { it.copy(graphicsQuality = quality) }
    }

    fun toggleMasterAudio() {
        val isAnySoundActive = _uiState.value.isMusicEnabled || _uiState.value.isSfxEnabled
        val next = !isAnySoundActive
        repo.isMusicEnabled = next
        audioEngine.setMusicEnabled(next, _uiState.value.selectedScenario.ambientDroneBaseFreq)
        repo.isSfxEnabled = next
        audioEngine.setSfxEnabled(next)
        _uiState.update { it.copy(isMusicEnabled = next, isSfxEnabled = next) }
    }

    fun toggleMusic() {
        val next = !_uiState.value.isMusicEnabled
        repo.isMusicEnabled = next
        audioEngine.setMusicEnabled(next, _uiState.value.selectedScenario.ambientDroneBaseFreq)
        _uiState.update { it.copy(isMusicEnabled = next) }
    }

    fun toggleSfx() {
        val next = !_uiState.value.isSfxEnabled
        repo.isSfxEnabled = next
        audioEngine.setSfxEnabled(next)
        _uiState.update { it.copy(isSfxEnabled = next) }
    }

    fun toggleHaptics() {
        val next = !_uiState.value.isHapticsEnabled
        repo.isHapticsEnabled = next
        audioEngine.setHapticsEnabled(next)
        _uiState.update { it.copy(isHapticsEnabled = next) }
    }

    fun toggleSpatialAudio() {
        val next = !_uiState.value.isSpatialAudioEnabled
        repo.isSpatialAudioEnabled = next
        audioEngine.setSpatialAudioEnabled(next)
        _uiState.update { it.copy(isSpatialAudioEnabled = next) }
    }

    fun setMusicVolume(volume: Float) {
        repo.musicVolume = volume
        audioEngine.setMusicVolume(volume)
        _uiState.update { it.copy(musicVolume = volume) }
    }

    fun setSfxVolume(volume: Float) {
        repo.sfxVolume = volume
        audioEngine.setSfxVolume(volume)
        _uiState.update { it.copy(sfxVolume = volume) }
    }

    fun setAmbientSoundMode(mode: AmbientSoundMode) {
        repo.ambientTrackMode = mode.name
        audioEngine.setAmbientMode(mode, _uiState.value.selectedScenario.ambientDroneBaseFreq)
        _uiState.update { it.copy(ambientSoundMode = mode) }
    }

    private fun unlockAchievement(id: String) {
        val achList = _uiState.value.achievements
        val currentAch = achList.firstOrNull { it.id == id } ?: return
        if (currentAch.isUnlocked) return

        repo.unlockAchievement(id)
        repo.addRewards(100, currentAch.rewardNanocores)
        audioEngine.playVictory()

        val updated = achList.map {
            if (it.id == id) it.copy(isUnlocked = true, progress = 1f) else it
        }

        _uiState.update {
            it.copy(
                achievements = updated,
                latestUnlockedAchievement = currentAch,
                nanocores = repo.nanocores,
                playerXp = repo.xp,
                playerLevel = repo.level
            )
        }
    }

    fun dismissAchievementToast() {
        _uiState.update { it.copy(latestUnlockedAchievement = null) }
    }

    fun setDialog(
        achievements: Boolean = false,
        cosmetics: Boolean = false,
        difficulty: Boolean = false,
        help: Boolean = false,
        language: Boolean = false
    ) {
        _uiState.update {
            it.copy(
                showAchievementsDialog = achievements,
                showCosmeticsDialog = cosmetics,
                showDifficultyDialog = difficulty,
                showHelpDialog = help,
                showLanguageDialog = language
            )
        }
    }

    fun setLanguageDialog(show: Boolean) {
        _uiState.update { it.copy(showLanguageDialog = show) }
    }

    fun setSettingsDialog(show: Boolean) {
        _uiState.update { it.copy(showSettingsDialog = show) }
    }

    fun playTestSound() {
        audioEngine.playTileMatch()
    }

    fun setLanguage(lang: AppLanguage) {
        repo.language = lang.code
        _uiState.update { it.copy(language = lang, showLanguageDialog = false) }
        audioEngine.playTileClick()
    }

    fun navigateToGame() {
        _uiState.update { it.copy(currentScreen = AppScreen.GAME) }
        audioEngine.playTileClick()
    }

    fun navigateToMainMenu() {
        _uiState.update { it.copy(currentScreen = AppScreen.MAIN_MENU) }
        audioEngine.playTileClick()
    }

    fun shareAchievement(achievement: Achievement, context: Context) {
        val text = achievement.generateShareText(_uiState.value.playerLevel, _uiState.value.score)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir Logro Cyber Mahjong")
        context.startActivity(shareIntent)
    }

    fun shareVictory(context: Context) {
        val text = """
            🏆 ¡Victoria en Cyber Mahjong! 🚀
            
            ⚡ Dificultad: ${_uiState.value.difficulty.title}
            ⭐ Puntuación: ${_uiState.value.score} pts
            ⏱️ Tiempo: ${_uiState.value.elapsedSeconds}s | Racha Máxima: ${_uiState.value.combo}x
            🀄 Fichas auténticas y efectos cibernéticos
            
            ¿Aceptas el desafío? #CyberMahjong #CyberpunkGaming
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir Récord de Mahjong")
        context.startActivity(shareIntent)
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        autoRotateJob?.cancel()
        gazeJob?.cancel()
        sensorManager.stopListening()
        audioEngine.release()
    }
}

class MahjongViewModelFactory(
    private val repo: GamePreferencesRepository,
    private val audioEngine: CyberAudioEngine,
    private val sensorManager: OrientationSensorManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MahjongViewModel(repo, audioEngine, sensorManager) as T
    }
}

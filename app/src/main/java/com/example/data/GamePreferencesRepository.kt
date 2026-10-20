package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.GraphicsQuality
import com.example.model.ScenarioTheme
import com.example.model.TileSkin

class GamePreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("cyber_mahjong_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_XP = "player_xp"
        private const val KEY_NANOCORES = "player_nanocores"
        private const val KEY_LEVEL = "player_level"
        private const val KEY_HIGH_SCORE = "high_score"
        private const val KEY_GAMES_WON = "games_won"
        private const val KEY_TILES_MATCHED = "tiles_matched"
        private const val KEY_SELECTED_SKIN = "selected_tile_skin"
        private const val KEY_SELECTED_SCENARIO = "selected_scenario"
        private const val KEY_GRAPHICS_QUALITY = "graphics_quality"
        private const val KEY_MUSIC_ENABLED = "music_enabled"
        private const val KEY_SFX_ENABLED = "sfx_enabled"
        private const val KEY_HAPTICS_ENABLED = "haptics_enabled"
        private const val KEY_SPATIAL_AUDIO = "spatial_audio_enabled"
        private const val KEY_MUSIC_VOLUME = "music_volume"
        private const val KEY_SFX_VOLUME = "sfx_volume"
        private const val KEY_AMBIENT_TRACK_MODE = "ambient_track_mode"
        private const val KEY_UNLOCKED_SKINS = "unlocked_skins"
        private const val KEY_UNLOCKED_SCENARIOS = "unlocked_scenarios"
        private const val KEY_UNLOCKED_ACHIEVEMENTS = "unlocked_achievements"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
        private const val KEY_LANGUAGE = "app_language"
    }

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "es") ?: "es"
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var isDarkMode: Boolean
        get() = prefs.getBoolean(KEY_IS_DARK_MODE, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_DARK_MODE, value).apply()

    var xp: Int
        get() = prefs.getInt(KEY_XP, 0)
        set(value) = prefs.edit().putInt(KEY_XP, value).apply()

    var nanocores: Int
        get() = prefs.getInt(KEY_NANOCORES, 200) // 200 starting nanocores welcome bonus
        set(value) = prefs.edit().putInt(KEY_NANOCORES, value).apply()

    var level: Int
        get() = prefs.getInt(KEY_LEVEL, 1)
        set(value) = prefs.edit().putInt(KEY_LEVEL, value).apply()

    var highScore: Int
        get() = prefs.getInt(KEY_HIGH_SCORE, 0)
        set(value) = prefs.edit().putInt(KEY_HIGH_SCORE, value).apply()

    var gamesWon: Int
        get() = prefs.getInt(KEY_GAMES_WON, 0)
        set(value) = prefs.edit().putInt(KEY_GAMES_WON, value).apply()

    var tilesMatched: Int
        get() = prefs.getInt(KEY_TILES_MATCHED, 0)
        set(value) = prefs.edit().putInt(KEY_TILES_MATCHED, value).apply()

    var selectedSkin: TileSkin
        get() {
            val id = prefs.getString(KEY_SELECTED_SKIN, TileSkin.JADE_IVORY.id)
            return TileSkin.values().firstOrNull { it.id == id } ?: TileSkin.JADE_IVORY
        }
        set(value) = prefs.edit().putString(KEY_SELECTED_SKIN, value.id).apply()

    var selectedScenario: ScenarioTheme
        get() {
            val id = prefs.getString(KEY_SELECTED_SCENARIO, ScenarioTheme.NEO_TOKYO.id)
            return ScenarioTheme.values().firstOrNull { it.id == id } ?: ScenarioTheme.NEO_TOKYO
        }
        set(value) = prefs.edit().putString(KEY_SELECTED_SCENARIO, value.id).apply()

    var graphicsQuality: GraphicsQuality
        get() {
            val name = prefs.getString(KEY_GRAPHICS_QUALITY, GraphicsQuality.BALANCED.name)
            return runCatching { GraphicsQuality.valueOf(name!!) }.getOrDefault(GraphicsQuality.BALANCED)
        }
        set(value) = prefs.edit().putString(KEY_GRAPHICS_QUALITY, value.name).apply()

    var isMusicEnabled: Boolean
        get() = prefs.getBoolean(KEY_MUSIC_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_MUSIC_ENABLED, value).apply()

    var isSfxEnabled: Boolean
        get() = prefs.getBoolean(KEY_SFX_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SFX_ENABLED, value).apply()

    var isHapticsEnabled: Boolean
        get() = prefs.getBoolean(KEY_HAPTICS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_HAPTICS_ENABLED, value).apply()

    var isSpatialAudioEnabled: Boolean
        get() = prefs.getBoolean(KEY_SPATIAL_AUDIO, true)
        set(value) = prefs.edit().putBoolean(KEY_SPATIAL_AUDIO, value).apply()

    var musicVolume: Float
        get() = prefs.getFloat(KEY_MUSIC_VOLUME, 0.75f)
        set(value) = prefs.edit().putFloat(KEY_MUSIC_VOLUME, value.coerceIn(0f, 1f)).apply()

    var sfxVolume: Float
        get() = prefs.getFloat(KEY_SFX_VOLUME, 0.85f)
        set(value) = prefs.edit().putFloat(KEY_SFX_VOLUME, value.coerceIn(0f, 1f)).apply()

    var ambientTrackMode: String
        get() = prefs.getString(KEY_AMBIENT_TRACK_MODE, "ZEN_DRONE") ?: "ZEN_DRONE"
        set(value) = prefs.edit().putString(KEY_AMBIENT_TRACK_MODE, value).apply()

    fun getUnlockedSkins(): Set<String> {
        val defaultSkins = setOf(TileSkin.JADE_IVORY.id, TileSkin.CYBERPUNK_NEON.id)
        return prefs.getStringSet(KEY_UNLOCKED_SKINS, defaultSkins) ?: defaultSkins
    }

    fun unlockSkin(skinId: String) {
        val current = getUnlockedSkins().toMutableSet()
        current.add(skinId)
        prefs.edit().putStringSet(KEY_UNLOCKED_SKINS, current).apply()
    }

    fun getUnlockedScenarios(): Set<String> {
        val defaultScenarios = setOf(ScenarioTheme.NEO_TOKYO.id)
        return prefs.getStringSet(KEY_UNLOCKED_SCENARIOS, defaultScenarios) ?: defaultScenarios
    }

    fun unlockScenario(scenarioId: String) {
        val current = getUnlockedScenarios().toMutableSet()
        current.add(scenarioId)
        prefs.edit().putStringSet(KEY_UNLOCKED_SCENARIOS, current).apply()
    }

    fun getUnlockedAchievements(): Set<String> {
        return prefs.getStringSet(KEY_UNLOCKED_ACHIEVEMENTS, emptySet()) ?: emptySet()
    }

    fun unlockAchievement(achievementId: String) {
        val current = getUnlockedAchievements().toMutableSet()
        current.add(achievementId)
        prefs.edit().putStringSet(KEY_UNLOCKED_ACHIEVEMENTS, current).apply()
    }

    fun addRewards(earnedXp: Int, earnedNanocores: Int) {
        xp += earnedXp
        nanocores += earnedNanocores
        // Level up every 500 XP
        val newLevel = 1 + (xp / 500)
        if (newLevel > level) {
            level = newLevel
        }
    }
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.audio.CyberAudioEngine
import com.example.data.GamePreferencesRepository
import com.example.model.AppScreen
import com.example.sensor.OrientationSensorManager
import com.example.ui.screens.MahjongGameScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.MahjongViewModel
import com.example.viewmodel.MahjongViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var audioEngine: CyberAudioEngine
    private lateinit var sensorManager: OrientationSensorManager
    private lateinit var preferencesRepo: GamePreferencesRepository

    private val viewModel: MahjongViewModel by viewModels {
        MahjongViewModelFactory(preferencesRepo, audioEngine, sensorManager)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        preferencesRepo = GamePreferencesRepository(applicationContext)
        audioEngine = CyberAudioEngine(applicationContext)
        sensorManager = OrientationSensorManager(applicationContext)
        com.example.ads.AdMobManager.initialize(applicationContext)

        setContent {
            val uiState by viewModel.uiState.collectAsState()
            MyApplicationTheme(darkTheme = uiState.isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (uiState.currentScreen) {
                        AppScreen.MAIN_MENU -> MainMenuScreen(viewModel = viewModel)
                        AppScreen.GAME -> MahjongGameScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::preferencesRepo.isInitialized && preferencesRepo.isMusicEnabled && ::audioEngine.isInitialized) {
            val mode = com.example.audio.AmbientSoundMode.values().firstOrNull { it.name == preferencesRepo.ambientTrackMode } ?: com.example.audio.AmbientSoundMode.ZEN_DRONE
            audioEngine.setAmbientMode(mode, preferencesRepo.selectedScenario.ambientDroneBaseFreq)
        }
    }

    override fun onPause() {
        super.onPause()
        if (::audioEngine.isInitialized) {
            audioEngine.stopAmbientDrone()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::audioEngine.isInitialized) {
            audioEngine.release()
        }
    }
}

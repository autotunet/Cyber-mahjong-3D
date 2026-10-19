package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Random
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.sin

enum class AmbientSoundMode(val title: String, val description: String) {
    ZEN_DRONE("Zen Cuántico 432Hz", "Ondas armónicas calmantes con respiración LFO lenta"),
    RAIN_CHILL("Lluvia Digital Chill", "Sonido de lluvia sintética suave con almohadillas graves"),
    COSMIC_FLOW("Flujo Cósmico", "Acordes etéreos en quinta con reflejos cristalinos")
}

class CyberAudioEngine(private val context: Context) {

    private val sampleRate = 44100
    private var isMusicEnabled = true
    private var isSfxEnabled = true
    private var isHapticsEnabled = true
    private var isSpatialEnabled = true

    private var musicVolume = 0.75f
    private var sfxVolume = 0.85f
    private var currentAmbientMode = AmbientSoundMode.ZEN_DRONE

    private var ambientJob: Job? = null
    private var ambientTrack: AudioTrack? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun setMusicEnabled(enabled: Boolean, baseFreq: Float = 110f) {
        isMusicEnabled = enabled
        if (enabled) {
            startAmbientDrone(baseFreq)
        } else {
            stopAmbientDrone()
        }
    }

    fun setSfxEnabled(enabled: Boolean) {
        isSfxEnabled = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        isHapticsEnabled = enabled
    }

    fun setSpatialAudioEnabled(enabled: Boolean) {
        isSpatialEnabled = enabled
    }

    fun setMusicVolume(volume: Float) {
        musicVolume = volume.coerceIn(0f, 1f)
    }

    fun setSfxVolume(volume: Float) {
        sfxVolume = volume.coerceIn(0f, 1f)
    }

    fun setAmbientMode(mode: AmbientSoundMode, baseFreq: Float = 110f) {
        currentAmbientMode = mode
        if (isMusicEnabled) {
            startAmbientDrone(baseFreq)
        }
    }

    fun startAmbientDrone(baseFreq: Float = 110f) {
        stopAmbientDrone()
        if (!isMusicEnabled) return

        ambientJob = scope.launch {
            val bufferSize = sampleRate / 2 // 0.5s stereo chunks (sampleRate samples = 0.5s)
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val audioFormat = AudioFormat.Builder()
                .setSampleRate(sampleRate)
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                .build()

            val track = AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setAudioFormat(audioFormat)
                .setBufferSizeInBytes(bufferSize * 4) // 2 channels * 2 bytes/sample
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            ambientTrack = track
            track.play()

            var phase1 = 0.0
            var phase2 = 0.0
            var phase3 = 0.0
            var lfoPhase = 0.0
            var pinkNoiseB0 = 0.0
            var pinkNoiseB1 = 0.0
            var pinkNoiseB2 = 0.0

            val stereoBuffer = ShortArray(bufferSize * 2)
            val random = Random()

            while (isActive && isMusicEnabled) {
                val mode = currentAmbientMode
                val vol = musicVolume

                for (i in 0 until bufferSize) {
                    val lfo = 0.85 + 0.15 * sin(lfoPhase)
                    var leftSample = 0.0
                    var rightSample = 0.0

                    when (mode) {
                        AmbientSoundMode.ZEN_DRONE -> {
                            val s1 = sin(phase1)
                            val s2 = 0.45 * sin(phase2)
                            val s3 = 0.28 * sin(phase3)
                            val core = (s1 + s2 + s3) / 1.73 * lfo * vol * 0.14

                            // Subtle stereo spread
                            leftSample = core * (0.95 + 0.05 * sin(lfoPhase * 0.7))
                            rightSample = core * (0.95 + 0.05 * cos(lfoPhase * 0.7))

                            phase1 += 2.0 * PI * baseFreq / sampleRate
                            phase2 += 2.0 * PI * (baseFreq * 1.5) / sampleRate // Perfect fifth
                            phase3 += 2.0 * PI * (baseFreq * 2.0) / sampleRate // Octave
                            lfoPhase += 2.0 * PI * 0.11 / sampleRate // Slow 0.11Hz breath
                        }

                        AmbientSoundMode.RAIN_CHILL -> {
                            // Synthesized rain filter (pink noise model)
                            val white = (random.nextDouble() * 2.0 - 1.0)
                            pinkNoiseB0 = 0.99886 * pinkNoiseB0 + white * 0.0555179
                            pinkNoiseB1 = 0.99332 * pinkNoiseB1 + white * 0.0750759
                            pinkNoiseB2 = 0.96900 * pinkNoiseB2 + white * 0.1538520
                            val rain = (pinkNoiseB0 + pinkNoiseB1 + pinkNoiseB2 + white * 0.5362) * 0.035 * vol

                            val sBass = 0.35 * sin(phase1) * vol * 0.12
                            leftSample = sBass + rain * (0.9 + 0.1 * random.nextDouble())
                            rightSample = sBass + rain * (0.9 + 0.1 * random.nextDouble())

                            phase1 += 2.0 * PI * (baseFreq * 0.75) / sampleRate
                            lfoPhase += 2.0 * PI * 0.08 / sampleRate
                        }

                        AmbientSoundMode.COSMIC_FLOW -> {
                            val s1 = sin(phase1)
                            val s2 = 0.4 * sin(phase2 + sin(lfoPhase))
                            val s3 = 0.25 * sin(phase3 + cos(lfoPhase))
                            val core = (s1 + s2 + s3) / 1.65 * lfo * vol * 0.15

                            leftSample = core * (0.9 + 0.1 * sin(lfoPhase))
                            rightSample = core * (0.9 + 0.1 * cos(lfoPhase))

                            phase1 += 2.0 * PI * baseFreq / sampleRate
                            phase2 += 2.0 * PI * (baseFreq * 1.4983) / sampleRate
                            phase3 += 2.0 * PI * (baseFreq * 2.5) / sampleRate
                            lfoPhase += 2.0 * PI * 0.18 / sampleRate
                        }
                    }

                    stereoBuffer[i * 2] = (leftSample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                    stereoBuffer[i * 2 + 1] = (rightSample.coerceIn(-1.0, 1.0) * Short.MAX_VALUE).toInt().toShort()
                }

                track.write(stereoBuffer, 0, stereoBuffer.size)
            }

            try {
                track.stop()
                track.release()
            } catch (_: Exception) {}
        }
    }

    fun stopAmbientDrone() {
        ambientJob?.cancel()
        ambientJob = null
        try {
            ambientTrack?.stop()
            ambientTrack?.release()
        } catch (_: Exception) {}
        ambientTrack = null
    }

    fun playTileClick() {
        playSpatialTileClick(0f, 0f)
    }

    fun playTileMatch(combo: Int = 1) {
        playSpatialTileMatch(-0.25f, 0.25f, combo)
    }

    /**
     * Spatial 3D Tile Click with stereo panning [-1.0f (left) to 1.0f (right)]
     */
    fun playSpatialTileClick(panNormalized: Float = 0f, depth: Float = 0f) {
        triggerHaptic(VibrationEffect.EFFECT_CLICK)
        if (!isSfxEnabled) return

        scope.launch {
            val pan = if (isSpatialEnabled) panNormalized.coerceIn(-1f, 1f) else 0f
            val panAngle = (pan + 1f) * (PI.toFloat() / 4f)
            val leftGain = cos(panAngle) * sfxVolume
            val rightGain = sin(panAngle) * sfxVolume

            val durationMs = 50
            val numFrames = (sampleRate * durationMs / 1000)
            val stereoBuffer = ShortArray(numFrames * 2)

            val baseClickFreq = 860f - (depth * 30f).coerceIn(0f, 200f)

            for (i in 0 until numFrames) {
                val t = i.toFloat() / sampleRate
                val freq = baseClickFreq - (t * 9000f)
                val decay = exp(-t * 75.0)
                val sample = sin(2.0 * PI * freq * t) * decay * 0.5

                stereoBuffer[i * 2] = (sample * leftGain * Short.MAX_VALUE).toInt().toShort()
                stereoBuffer[i * 2 + 1] = (sample * rightGain * Short.MAX_VALUE).toInt().toShort()
            }

            playPcmBufferStereo(stereoBuffer)
        }
    }

    /**
     * Spatial 3D Match chime that sweeps stereo field from tile 1 to tile 2!
     */
    fun playSpatialTileMatch(pan1: Float = -0.3f, pan2: Float = 0.3f, combo: Int = 1) {
        triggerHaptic(VibrationEffect.EFFECT_HEAVY_CLICK)
        if (!isSfxEnabled) return

        scope.launch {
            val durationMs = 380
            val numFrames = (sampleRate * durationMs / 1000)
            val stereoBuffer = ShortArray(numFrames * 2)

            val baseC = 1046.5f * (1f + (combo - 1) * 0.08f)
            val noteE = baseC * 1.2599f
            val noteG = baseC * 1.4983f
            val noteC2 = baseC * 2.0f

            val p1 = if (isSpatialEnabled) pan1.coerceIn(-1f, 1f) else 0f
            val p2 = if (isSpatialEnabled) pan2.coerceIn(-1f, 1f) else 0f

            for (i in 0 until numFrames) {
                val t = i.toFloat() / sampleRate
                val progress = (i.toFloat() / numFrames).coerceIn(0f, 1f)

                // Stereo sweep between the two matching tiles
                val currentPan = p1 + (p2 - p1) * progress
                val panAngle = (currentPan + 1f) * (PI.toFloat() / 4f)
                val leftGain = cos(panAngle) * sfxVolume
                val rightGain = sin(panAngle) * sfxVolume

                val decay = exp(-t * 10.5)
                val s1 = sin(2.0 * PI * baseC * t)
                val s2 = sin(2.0 * PI * noteE * t)
                val s3 = sin(2.0 * PI * noteG * t)
                val s4 = 0.4 * sin(2.0 * PI * noteC2 * t)
                val chime = (s1 + s2 + s3 + s4) / 3.4 * decay * 0.65

                stereoBuffer[i * 2] = (chime * leftGain * Short.MAX_VALUE).toInt().toShort()
                stereoBuffer[i * 2 + 1] = (chime * rightGain * Short.MAX_VALUE).toInt().toShort()
            }

            playPcmBufferStereo(stereoBuffer)
        }
    }

    fun playError() {
        triggerHaptic(VibrationEffect.EFFECT_DOUBLE_CLICK)
        if (!isSfxEnabled) return

        scope.launch {
            val durationMs = 120
            val numFrames = (sampleRate * durationMs / 1000)
            val stereoBuffer = ShortArray(numFrames * 2)

            for (i in 0 until numFrames) {
                val t = i.toFloat() / sampleRate
                val decay = exp(-t * 18.0)
                val s = sin(2.0 * PI * 180.0 * t) * decay * 0.4 * sfxVolume
                stereoBuffer[i * 2] = (s * Short.MAX_VALUE).toInt().toShort()
                stereoBuffer[i * 2 + 1] = (s * Short.MAX_VALUE).toInt().toShort()
            }
            playPcmBufferStereo(stereoBuffer)
        }
    }

    fun playVictory() {
        triggerHaptic(VibrationEffect.EFFECT_HEAVY_CLICK)
        if (!isSfxEnabled) return

        scope.launch {
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f, 1318.51f)
            val noteDurMs = 130
            val numFramesPerNote = (sampleRate * noteDurMs / 1000)
            val totalFrames = numFramesPerNote * notes.size
            val stereoBuffer = ShortArray(totalFrames * 2)

            notes.forEachIndexed { noteIdx, freq ->
                val notePan = (-0.6f + (noteIdx.toFloat() / (notes.size - 1)) * 1.2f).coerceIn(-1f, 1f)
                val panAngle = (notePan + 1f) * (PI.toFloat() / 4f)
                val leftGain = cos(panAngle) * sfxVolume
                val rightGain = sin(panAngle) * sfxVolume

                for (i in 0 until numFramesPerNote) {
                    val t = i.toFloat() / sampleRate
                    val decay = exp(-t * 7.5)
                    val s = sin(2.0 * PI * freq * t) * decay * 0.55
                    val frameIdx = (noteIdx * numFramesPerNote + i) * 2
                    stereoBuffer[frameIdx] = (s * leftGain * Short.MAX_VALUE).toInt().toShort()
                }
            }
            playPcmBufferStereo(stereoBuffer)
        }
    }

    fun playTimerTick(isUrgent: Boolean = false) {
        if (!isSfxEnabled) return
        if (isUrgent) {
            triggerHaptic(VibrationEffect.EFFECT_TICK)
        }

        scope.launch {
            val freq = if (isUrgent) 880.0 else 440.0
            val durationMs = if (isUrgent) 60 else 35
            val numFrames = (sampleRate * durationMs / 1000)
            val stereoBuffer = ShortArray(numFrames * 2)

            for (i in 0 until numFrames) {
                val t = i.toFloat() / sampleRate
                val decay = exp(-t * 45.0)
                val s = sin(2.0 * PI * freq * t) * decay * (if (isUrgent) 0.45 else 0.25) * sfxVolume
                val sampleShort = (s * Short.MAX_VALUE).toInt().toShort()
                stereoBuffer[i * 2] = sampleShort
                stereoBuffer[i * 2 + 1] = sampleShort
            }
            playPcmBufferStereo(stereoBuffer)
        }
    }

    fun playShuffle() {
        triggerHaptic(VibrationEffect.EFFECT_TICK)
        if (!isSfxEnabled) return

        scope.launch {
            val durationMs = 200
            val numFrames = (sampleRate * durationMs / 1000)
            val stereoBuffer = ShortArray(numFrames * 2)

            for (i in 0 until numFrames) {
                val t = i.toFloat() / sampleRate
                val freq = 380.0 + sin(t * 70.0) * 220.0
                val decay = exp(-t * 11.0)
                val s = sin(2.0 * PI * freq * t) * decay * 0.38 * sfxVolume

                // Shuffling stereo ripple
                val pan = sin(t * 40.0).toFloat() * 0.5f
                val panAngle = (pan + 1f) * (PI.toFloat() / 4f)
                val leftGain = cos(panAngle)
                val rightGain = sin(panAngle)

                stereoBuffer[i * 2] = (s * leftGain * Short.MAX_VALUE).toInt().toShort()
                stereoBuffer[i * 2 + 1] = (s * rightGain * Short.MAX_VALUE).toInt().toShort()
            }
            playPcmBufferStereo(stereoBuffer)
        }
    }

    private fun playPcmBufferStereo(buffer: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setSampleRate(sampleRate)
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_STEREO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            track.setNotificationMarkerPosition(buffer.size / 2) // frames count
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) {
                    t?.release()
                }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
        } catch (_: Exception) {}
    }

    private fun triggerHaptic(effectId: Int) {
        if (!isHapticsEnabled || vibrator == null || !vibrator!!.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(effectId))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {}
    }

    fun release() {
        stopAmbientDrone()
    }
}

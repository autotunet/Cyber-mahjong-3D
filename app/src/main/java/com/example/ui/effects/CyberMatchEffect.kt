package com.example.ui.effects

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.GraphicsQuality
import com.example.model.MatchEffectEvent
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberMagenta
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * High-velocity motion-blurred photon streak particle.
 */
data class CyberSparkParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var alpha: Float = 1f,
    var size: Float,
    var life: Float = 1f,
    val maxLife: Float = 1f
)

/**
 * Geometric cyber shard that spins in 2D space.
 */
data class HoloShardParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var rotation: Float,
    var spinRate: Float,
    var size: Float,
    var color: Color,
    var alpha: Float = 1f,
    var life: Float = 1f
)

/**
 * Telemetry and matrix data packet rising into the ether.
 */
data class DigitalGlyphParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var glyph: String,
    var color: Color,
    var alpha: Float = 1f,
    var life: Float = 1f
)

/**
 * Cybernetic packet travelling along the laser beam conduit.
 */
data class BeamDataPacket(
    var progress: Float,
    var speed: Float,
    var color: Color
)

@Composable
fun CyberMatchEffectOverlay(
    matchEvent: MatchEffectEvent?,
    tile1Center: Offset?,
    tile2Center: Offset?,
    quality: GraphicsQuality,
    textMeasurer: TextMeasurer,
    modifier: Modifier = Modifier
) {
    val sparks = remember { mutableStateListOf<CyberSparkParticle>() }
    val shards = remember { mutableStateListOf<HoloShardParticle>() }
    val glyphs = remember { mutableStateListOf<DigitalGlyphParticle>() }
    val packets = remember { mutableStateListOf<BeamDataPacket>() }

    // Animation drivers
    val glowProgress = remember { Animatable(0f) }
    val beamProgress = remember { Animatable(0f) }
    val shockwaveProgress = remember { Animatable(0f) }
    val reticleRotation = remember { Animatable(0f) }
    val floatingTextProgress = remember { Animatable(0f) }

    // Spawn cybernetic particles and trigger animations on match
    LaunchedEffect(matchEvent?.eventId) {
        if (matchEvent != null && tile1Center != null && tile2Center != null) {
            sparks.clear()
            shards.clear()
            glyphs.clear()
            packets.clear()

            val sparkCount = when (quality) {
                GraphicsQuality.LOW -> 18
                GraphicsQuality.BALANCED -> 36
                GraphicsQuality.ULTRA_CYBER -> 64
            }
            val shardCount = when (quality) {
                GraphicsQuality.LOW -> 4
                GraphicsQuality.BALANCED -> 10
                GraphicsQuality.ULTRA_CYBER -> 18
            }
            val glyphList = listOf("01", "10", "⚡", "◈", "◇", "0x", "λ", "✦", "⬡", "OK")

            // Spawn particles at both tile centers
            for (center in listOf(tile1Center, tile2Center)) {
                for (i in 0 until sparkCount) {
                    val angle = (Math.random() * 2.0 * PI).toFloat()
                    val speed = (80f + Math.random() * 320f).toFloat()
                    val life = (0.5f + Math.random() * 0.5f).toFloat()
                    val color = when (i % 4) {
                        0 -> CyberCyan
                        1 -> CyberMagenta
                        2 -> CyberGold
                        else -> Color.White
                    }
                    sparks.add(
                        CyberSparkParticle(
                            x = center.x,
                            y = center.y,
                            vx = cos(angle) * speed,
                            vy = sin(angle) * speed,
                            color = color,
                            size = (2.5f + Math.random() * 4.5f).toFloat(),
                            life = life,
                            maxLife = life
                        )
                    )
                }

                if (quality != GraphicsQuality.LOW) {
                    for (i in 0 until shardCount) {
                        val angle = (Math.random() * 2.0 * PI).toFloat()
                        val speed = (40f + Math.random() * 140f).toFloat()
                        shards.add(
                            HoloShardParticle(
                                x = center.x,
                                y = center.y,
                                vx = cos(angle) * speed,
                                vy = sin(angle) * speed,
                                rotation = (Math.random() * 360f).toFloat(),
                                spinRate = (-240f + Math.random() * 480f).toFloat(),
                                size = (5f + Math.random() * 7f).toFloat(),
                                color = if (i % 2 == 0) CyberCyan else CyberMagenta
                            )
                        )
                    }

                    for (i in 0 until 6) {
                        val angle = (Math.random() * 2.0 * PI).toFloat()
                        val speed = (25f + Math.random() * 75f).toFloat()
                        glyphs.add(
                            DigitalGlyphParticle(
                                x = center.x,
                                y = center.y,
                                vx = cos(angle) * speed,
                                vy = sin(angle) * speed - 30f,
                                glyph = glyphList.random(),
                                color = if (i % 2 == 0) CyberCyan else CyberGreen
                            )
                        )
                    }
                }
            }

            // Beam packets
            packets.add(BeamDataPacket(progress = 0f, speed = 2.4f, color = CyberCyan))
            packets.add(BeamDataPacket(progress = 1f, speed = -2.4f, color = CyberMagenta))

            // Animate concurrent effects
            glowProgress.snapTo(1f)
            beamProgress.snapTo(1f)
            shockwaveProgress.snapTo(0f)
            reticleRotation.snapTo(0f)
            floatingTextProgress.snapTo(0f)

            launch {
                glowProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing)
                )
            }
            launch {
                shockwaveProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                )
            }
            launch {
                reticleRotation.animateTo(
                    targetValue = 90f,
                    animationSpec = tween(durationMillis = 500, easing = LinearEasing)
                )
            }
            launch {
                beamProgress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 400, easing = LinearEasing)
                )
            }
            launch {
                floatingTextProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
                )
            }
        }
    }

    // 60fps VSync-driven physics loop using withFrameNanos
    LaunchedEffect(matchEvent?.eventId) {
        if (matchEvent == null) return@LaunchedEffect
        var lastNanos = 0L

        while (sparks.isNotEmpty() || shards.isNotEmpty() || glyphs.isNotEmpty() || packets.isNotEmpty()) {
            withFrameNanos { nowNanos ->
                if (lastNanos == 0L) {
                    lastNanos = nowNanos
                    return@withFrameNanos
                }
                val dt = ((nowNanos - lastNanos) / 1_000_000_000f).coerceIn(0.001f, 0.05f)
                lastNanos = nowNanos

                // Sparks update
                val sparkIter = sparks.iterator()
                while (sparkIter.hasNext()) {
                    val p = sparkIter.next()
                    p.x += p.vx * dt
                    p.y += p.vy * dt
                    p.vy += 90f * dt // slight gravity
                    p.vx *= 0.95f // air drag
                    p.life -= dt * 1.5f
                    p.alpha = (p.life / p.maxLife).coerceIn(0f, 1f)
                    if (p.life <= 0f) sparkIter.remove()
                }

                // Shards update
                val shardIter = shards.iterator()
                while (shardIter.hasNext()) {
                    val sh = shardIter.next()
                    sh.x += sh.vx * dt
                    sh.y += sh.vy * dt
                    sh.rotation += sh.spinRate * dt
                    sh.vx *= 0.96f
                    sh.vy *= 0.96f
                    sh.life -= dt * 1.6f
                    sh.alpha = sh.life.coerceIn(0f, 1f)
                    if (sh.life <= 0f) shardIter.remove()
                }

                // Glyphs update
                val glyphIter = glyphs.iterator()
                while (glyphIter.hasNext()) {
                    val g = glyphIter.next()
                    g.x += g.vx * dt
                    g.y += g.vy * dt
                    g.life -= dt * 1.3f
                    g.alpha = g.life.coerceIn(0f, 1f)
                    if (g.life <= 0f) glyphIter.remove()
                }

                // Beam packets update
                val packetIter = packets.iterator()
                while (packetIter.hasNext()) {
                    val pk = packetIter.next()
                    pk.progress += pk.speed * dt
                    if (pk.progress > 1f || pk.progress < 0f) {
                        packetIter.remove()
                    }
                }
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        if (tile1Center != null && tile2Center != null) {
            val glow = glowProgress.value
            val beamAlpha = beamProgress.value
            val shock = shockwaveProgress.value
            val rot = reticleRotation.value

            // 1. TILE CYBER GLOW AURA (Radial Glow + Pulsing Reticles)
            if (glow > 0.01f) {
                for (center in listOf(tile1Center, tile2Center)) {
                    val glowRadius = 55f + (1f - glow) * 45f

                    // Soft luminous radial corona
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = glow * 0.95f),
                                CyberCyan.copy(alpha = glow * 0.75f),
                                CyberMagenta.copy(alpha = glow * 0.35f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = glowRadius
                        ),
                        radius = glowRadius,
                        center = center
                    )

                    // Rotating Cyber Reticle (Segmented Ring)
                    rotate(degrees = rot, pivot = center) {
                        drawArc(
                            color = CyberCyan.copy(alpha = glow * 0.9f),
                            startAngle = 0f,
                            sweepAngle = 60f,
                            useCenter = false,
                            topLeft = Offset(center.x - 30f, center.y - 30f),
                            size = Size(60f, 60f),
                            style = Stroke(width = 2.2f)
                        )
                        drawArc(
                            color = CyberCyan.copy(alpha = glow * 0.9f),
                            startAngle = 120f,
                            sweepAngle = 60f,
                            useCenter = false,
                            topLeft = Offset(center.x - 30f, center.y - 30f),
                            size = Size(60f, 60f),
                            style = Stroke(width = 2.2f)
                        )
                        drawArc(
                            color = CyberCyan.copy(alpha = glow * 0.9f),
                            startAngle = 240f,
                            sweepAngle = 60f,
                            useCenter = false,
                            topLeft = Offset(center.x - 30f, center.y - 30f),
                            size = Size(60f, 60f),
                            style = Stroke(width = 2.2f)
                        )
                    }

                    // Counter-rotating Inner Ring
                    rotate(degrees = -rot * 1.5f, pivot = center) {
                        drawArc(
                            color = CyberMagenta.copy(alpha = glow * 0.8f),
                            startAngle = 30f,
                            sweepAngle = 80f,
                            useCenter = false,
                            topLeft = Offset(center.x - 20f, center.y - 20f),
                            size = Size(40f, 40f),
                            style = Stroke(width = 1.8f)
                        )
                        drawArc(
                            color = CyberMagenta.copy(alpha = glow * 0.8f),
                            startAngle = 210f,
                            sweepAngle = 80f,
                            useCenter = false,
                            topLeft = Offset(center.x - 20f, center.y - 20f),
                            size = Size(40f, 40f),
                            style = Stroke(width = 1.8f)
                        )
                    }

                    // Sci-Fi Lock Brackets [ ]
                    val bW = 24f
                    val bH = 32f
                    val bColor = Color.White.copy(alpha = glow * 0.85f)
                    val bStroke = 1.8f

                    // Top-Left corner
                    drawLine(bColor, Offset(center.x - bW, center.y - bH), Offset(center.x - bW + 8f, center.y - bH), bStroke)
                    drawLine(bColor, Offset(center.x - bW, center.y - bH), Offset(center.x - bW, center.y - bH + 8f), bStroke)

                    // Top-Right corner
                    drawLine(bColor, Offset(center.x + bW, center.y - bH), Offset(center.x + bW - 8f, center.y - bH), bStroke)
                    drawLine(bColor, Offset(center.x + bW, center.y - bH), Offset(center.x + bW, center.y - bH + 8f), bStroke)

                    // Bottom-Left corner
                    drawLine(bColor, Offset(center.x - bW, center.y + bH), Offset(center.x - bW + 8f, center.y + bH), bStroke)
                    drawLine(bColor, Offset(center.x - bW, center.y + bH), Offset(center.x - bW, center.y + bH - 8f), bStroke)

                    // Bottom-Right corner
                    drawLine(bColor, Offset(center.x + bW, center.y + bH), Offset(center.x + bW - 8f, center.y + bH), bStroke)
                    drawLine(bColor, Offset(center.x + bW, center.y + bH), Offset(center.x + bW, center.y + bH - 8f), bStroke)
                }
            }

            // 2. QUANTUM CONNECTING LASER BEAM + ELECTRIC LIGHTNING ARC
            if (beamAlpha > 0.01f) {
                // Wide outer plasma haze
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            CyberCyan.copy(alpha = beamAlpha * 0.45f),
                            CyberMagenta.copy(alpha = beamAlpha * 0.45f)
                        ),
                        start = tile1Center,
                        end = tile2Center
                    ),
                    start = tile1Center,
                    end = tile2Center,
                    strokeWidth = 18f * beamAlpha,
                    cap = StrokeCap.Round
                )

                // Intense laser core wire
                drawLine(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color.White.copy(alpha = beamAlpha * 0.95f),
                            CyberCyan.copy(alpha = beamAlpha * 0.95f)
                        ),
                        start = tile1Center,
                        end = tile2Center
                    ),
                    start = tile1Center,
                    end = tile2Center,
                    strokeWidth = 3.5f * beamAlpha,
                    cap = StrokeCap.Round
                )

                // High-frequency electric lightning arc
                val dx = tile2Center.x - tile1Center.x
                val dy = tile2Center.y - tile1Center.y
                val dist = kotlin.math.sqrt(dx * dx + dy * dy)
                if (dist > 10f) {
                    val nx = -dy / dist
                    val ny = dx / dist
                    val lightningPath = Path().apply {
                        moveTo(tile1Center.x, tile1Center.y)
                        val steps = 6
                        for (s in 1 until steps) {
                            val t = s.toFloat() / steps
                            val jitter = ((Math.random() - 0.5) * 22.0 * beamAlpha).toFloat()
                            val px = tile1Center.x + dx * t + nx * jitter
                            val py = tile1Center.y + dy * t + ny * jitter
                            lineTo(px, py)
                        }
                        lineTo(tile2Center.x, tile2Center.y)
                    }
                    drawPath(
                        path = lightningPath,
                        color = Color.White.copy(alpha = beamAlpha * 0.9f),
                        style = Stroke(width = 1.5f, cap = StrokeCap.Round)
                    )
                }

                // Data spark pulses along the laser line
                for (pk in packets) {
                    val px = tile1Center.x + (tile2Center.x - tile1Center.x) * pk.progress
                    val py = tile1Center.y + (tile2Center.y - tile1Center.y) * pk.progress
                    drawCircle(
                        color = pk.color.copy(alpha = beamAlpha),
                        radius = 5f * beamAlpha,
                        center = Offset(px, py)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = beamAlpha),
                        radius = 2.5f * beamAlpha,
                        center = Offset(px, py)
                    )
                }
            }

            // 3. CHROMATIC ABERRATION SHOCKWAVE RINGS
            if (shock in 0.01f..0.99f) {
                val shockAlpha = (1f - shock).coerceIn(0f, 1f)
                val shockRadius = 15f + (shock * 75f)

                for (center in listOf(tile1Center, tile2Center)) {
                    // Cyan ring (offset left/up)
                    drawCircle(
                        color = CyberCyan.copy(alpha = shockAlpha * 0.85f),
                        radius = shockRadius,
                        center = Offset(center.x - 2f, center.y - 1f),
                        style = Stroke(width = 2.5f * shockAlpha)
                    )
                    // Magenta ring (offset right/down)
                    drawCircle(
                        color = CyberMagenta.copy(alpha = shockAlpha * 0.7f),
                        radius = shockRadius,
                        center = Offset(center.x + 2f, center.y + 1f),
                        style = Stroke(width = 2.0f * shockAlpha)
                    )
                }
            }
        }

        // 4. MOTION-BLURRED PHOTON SPARKS (Streak lines + Glowing core)
        for (p in sparks) {
            val tailX = p.x - p.vx * 0.045f
            val tailY = p.y - p.vy * 0.045f

            // Motion streak trail
            drawLine(
                color = p.color.copy(alpha = p.alpha * 0.7f),
                start = Offset(tailX, tailY),
                end = Offset(p.x, p.y),
                strokeWidth = (p.size * 0.75f) * p.alpha,
                cap = StrokeCap.Round
            )
            // Luminous spark tip
            drawCircle(
                color = Color.White.copy(alpha = p.alpha * 0.9f),
                radius = p.size * 0.6f * p.alpha,
                center = Offset(p.x, p.y)
            )
        }

        // 5. ROTATING HOLOGRAPHIC SHARDS (Diamond Polygons)
        for (sh in shards) {
            rotate(degrees = sh.rotation, pivot = Offset(sh.x, sh.y)) {
                val half = sh.size * sh.alpha
                val diamondPath = Path().apply {
                    moveTo(sh.x, sh.y - half * 1.3f)
                    lineTo(sh.x + half, sh.y)
                    lineTo(sh.x, sh.y + half * 1.3f)
                    lineTo(sh.x - half, sh.y)
                    close()
                }
                drawPath(
                    path = diamondPath,
                    color = sh.color.copy(alpha = sh.alpha * 0.4f)
                )
                drawPath(
                    path = diamondPath,
                    color = sh.color.copy(alpha = sh.alpha * 0.85f),
                    style = Stroke(width = 1.2f)
                )
            }
        }

        // 6. RISING DIGITAL GLYPHS
        for (g in glyphs) {
            val glyphStyle = TextStyle(
                color = g.color.copy(alpha = g.alpha),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            val measured = textMeasurer.measure(g.glyph, style = glyphStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(g.x - measured.size.width / 2f, g.y)
            )
        }

        // 7. FLOATING HOLOGRAPHIC SCORE & COMBO POPUP CHIP
        if (matchEvent != null && tile1Center != null && tile2Center != null) {
            val floatVal = floatingTextProgress.value
            if (floatVal in 0.01f..0.99f) {
                val midX = (tile1Center.x + tile2Center.x) / 2f
                val midY = (tile1Center.y + tile2Center.y) / 2f - (floatVal * 75f)
                val textAlpha = (1f - floatVal).coerceIn(0f, 1f)

                val scoreText = "+${matchEvent.points} PTS"
                val scoreStyle = TextStyle(
                    color = CyberGold.copy(alpha = textAlpha),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                val measuredScore = textMeasurer.measure(scoreText, style = scoreStyle)

                // Holographic card badge behind score
                val badgeWidth = measuredScore.size.width + 36f
                val badgeHeight = measuredScore.size.height + (if (matchEvent.combo > 1) 32f else 14f)
                val badgeLeft = midX - badgeWidth / 2f
                val badgeTop = midY - 6f

                drawRoundRect(
                    color = Color(0xFF030D18).copy(alpha = textAlpha * 0.85f),
                    topLeft = Offset(badgeLeft, badgeTop),
                    size = Size(badgeWidth, badgeHeight),
                    cornerRadius = CornerRadius(10f, 10f)
                )
                drawRoundRect(
                    brush = Brush.horizontalGradient(
                        listOf(CyberCyan.copy(alpha = textAlpha * 0.9f), CyberMagenta.copy(alpha = textAlpha * 0.9f))
                    ),
                    topLeft = Offset(badgeLeft, badgeTop),
                    size = Size(badgeWidth, badgeHeight),
                    cornerRadius = CornerRadius(10f, 10f),
                    style = Stroke(width = 1.5f)
                )

                // Score text
                drawText(
                    textLayoutResult = measuredScore,
                    topLeft = Offset(midX - measuredScore.size.width / 2f, midY)
                )

                // Combo streak badge
                if (matchEvent.combo > 1) {
                    val comboText = "⚡ x${matchEvent.combo} COMBO!"
                    val comboStyle = TextStyle(
                        color = CyberMagenta.copy(alpha = textAlpha),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                    val measuredCombo = textMeasurer.measure(comboText, style = comboStyle)
                    drawText(
                        textLayoutResult = measuredCombo,
                        topLeft = Offset(midX - measuredCombo.size.width / 2f, midY + measuredScore.size.height + 4f)
                    )
                }
            }
        }
    }
}

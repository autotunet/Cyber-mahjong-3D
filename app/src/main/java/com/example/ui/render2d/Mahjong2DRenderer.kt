package com.example.ui.render2d

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.GraphicsQuality
import com.example.model.MahjongLayout
import com.example.model.MahjongTile
import com.example.model.ScenarioTheme
import com.example.model.TileCategory
import com.example.model.TileSkin
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

data class ProjectedTile2D(
    val tile: MahjongTile,
    val screenX: Float,
    val screenY: Float,
    val width: Float,
    val height: Float,
    val isPlayable: Boolean
)

object Mahjong2DRenderer {

    fun layoutTiles2D(
        tiles: List<MahjongTile>,
        center: Offset,
        zoom: Float,
        pan: Offset,
        baseTileWidth: Float = 64f,
        baseTileHeight: Float = 84f
    ): List<ProjectedTile2D> {
        val tW = baseTileWidth * zoom
        val tH = baseTileHeight * zoom
        val layerStepX = 3.5f * zoom
        val layerStepY = 4.5f * zoom

        val projected = ArrayList<ProjectedTile2D>(tiles.size)

        for (tile in tiles) {
            // Eliminated tiles are immediately hidden so underlying tiles are clear and free
            if (tile.isMatched) continue

            // 2D orthogonal grid position (2.0f in gridX = 1 tile width; 2.6f in gridY = 1 tile height)
            val sx = center.x + pan.x + (tile.gridX * (tW * 0.5f)) - (tile.gridZ * layerStepX)
            val sy = center.y + pan.y + ((tile.gridY / 2.6f) * tH) - (tile.gridZ * layerStepY)

            val playable = MahjongLayout.isTilePlayable(tile, tiles)

            projected.add(
                ProjectedTile2D(
                    tile = tile,
                    screenX = sx,
                    screenY = sy,
                    width = tW,
                    height = tH,
                    isPlayable = playable
                )
            )
        }

        // Sort by Z layer ascending so upper layers render on top
        projected.sortBy { it.tile.gridZ }
        return projected
    }

    fun computeTileScreenCenter(
        gridX: Float,
        gridY: Float,
        gridZ: Int,
        center: Offset,
        zoom: Float,
        pan: Offset,
        baseTileWidth: Float = 64f,
        baseTileHeight: Float = 84f
    ): Offset {
        val tW = baseTileWidth * zoom
        val tH = baseTileHeight * zoom
        val layerStepX = 3.5f * zoom
        val layerStepY = 4.5f * zoom
        val sx = center.x + pan.x + (gridX * (tW * 0.5f)) - (gridZ * layerStepX)
        val sy = center.y + pan.y + ((gridY / 2.6f) * tH) - (gridZ * layerStepY)
        return Offset(sx, sy)
    }

    fun findTileAtScreenPoint2D(
        point: Offset,
        projectedTiles: List<ProjectedTile2D>
    ): MahjongTile? {
        // Iterate backwards from top layer to bottom
        for (i in projectedTiles.indices.reversed()) {
            val pt = projectedTiles[i]
            if (pt.tile.isMatched) continue // Matched tiles can never block touch inputs

            val left = pt.screenX - pt.width / 2f
            val top = pt.screenY - pt.height / 2f
            val right = left + pt.width
            val bottom = top + pt.height

            if (point.x in left..right && point.y in top..bottom) {
                return pt.tile
            }
        }
        return null
    }

    fun draw2DGridFloor(
        drawScope: DrawScope,
        center: Offset,
        pan: Offset,
        zoom: Float,
        scenario: ScenarioTheme,
        isDarkMode: Boolean = true
    ) {
        val gridColor = if (isDarkMode) Color(scenario.gridColorHex) else Color(0xFF64748B)
        val alpha = if (isDarkMode) 0.22f else 0.15f
        val step = 48f * zoom
        val lineCount = 12
        val extent = lineCount * step

        for (i in -lineCount..lineCount) {
            val offsetVal = i * step
            // Vertical line
            drawScope.drawLine(
                color = gridColor.copy(alpha = alpha),
                start = Offset(center.x + pan.x + offsetVal, center.y + pan.y - extent),
                end = Offset(center.x + pan.x + offsetVal, center.y + pan.y + extent),
                strokeWidth = 1f
            )
            // Horizontal line
            drawScope.drawLine(
                color = gridColor.copy(alpha = alpha),
                start = Offset(center.x + pan.x - extent, center.y + pan.y + offsetVal),
                end = Offset(center.x + pan.x + extent, center.y + pan.y + offsetVal),
                strokeWidth = 1f
            )
        }
    }

    fun drawTile2D(
        drawScope: DrawScope,
        projected: ProjectedTile2D,
        skin: TileSkin,
        quality: GraphicsQuality,
        textMeasurer: TextMeasurer,
        isDarkMode: Boolean = true
    ) {
        val tile = projected.tile
        val alpha = (1f - tile.dissolveProgress).coerceIn(0f, 1f)
        if (alpha <= 0.01f) return

        val w = projected.width
        val h = projected.height

        val elevationLift = if (tile.isSelected) 7f else 0f
        val x = projected.screenX - w / 2f
        val y = projected.screenY - h / 2f - elevationLift

        val isLightFace = skin.isLightFace || !isDarkMode

        val baseBodyColor = if (isLightFace) Color(0xFFFCFAF4).copy(alpha = alpha) else Color(skin.bodyColorHex).copy(alpha = alpha)
        val backingColor = Color(skin.backingColorHex).copy(alpha = alpha)
        val primaryAccent = Color(skin.primaryAccentHex).copy(alpha = alpha)

        val cr = (w * 0.11f).coerceIn(5f, 9f)
        val sideThicknessW = (w * 0.055f).coerceIn(2.5f, 4.5f)
        val sideThicknessH = (h * 0.055f).coerceIn(3f, 5.5f)

        // 1. Ambient & Directional Drop Shadow
        if (quality.enableRealtimeShadows) {
            val shadowOffset = (3f + tile.gridZ * 2.8f + (if (tile.isSelected) 6f else 0f))
            drawScope.drawRoundRect(
                color = Color.Black.copy(alpha = (if (isDarkMode) 0.38f else 0.22f) * alpha),
                topLeft = Offset(x + shadowOffset, y + shadowOffset),
                size = Size(w, h),
                cornerRadius = CornerRadius(cr, cr)
            )
        }

        // 2. Physical 3D Tile Backing Slab (Green Bamboo or Cyber Acrylic backing)
        val backingDarker = backingColor.copy(
            red = (backingColor.red * 0.75f).coerceIn(0f, 1f),
            green = (backingColor.green * 0.75f).coerceIn(0f, 1f),
            blue = (backingColor.blue * 0.75f).coerceIn(0f, 1f)
        )

        // Bottom thickness bevel
        drawScope.drawRoundRect(
            color = backingDarker,
            topLeft = Offset(x, y + h - sideThicknessH - 1f),
            size = Size(w, sideThicknessH + 1f),
            cornerRadius = CornerRadius(cr, cr)
        )

        // Right thickness bevel
        drawScope.drawRoundRect(
            color = backingColor,
            topLeft = Offset(x + w - sideThicknessW - 1f, y),
            size = Size(sideThicknessW + 1f, h),
            cornerRadius = CornerRadius(cr, cr)
        )

        // Base tile backing rim
        drawScope.drawRoundRect(
            color = backingColor,
            topLeft = Offset(x, y),
            size = Size(w, h),
            cornerRadius = CornerRadius(cr, cr)
        )

        // 3. Tile Front Porcelain/Ivory/Obsidian Face
        val faceW = w - sideThicknessW * 0.5f
        val faceH = h - sideThicknessH * 0.5f

        val topFaceBrush = when {
            tile.isSelected -> {
                val selectTint = if (isDarkMode) Color(0xFF00F0FF) else Color(0xFF0284C7)
                Brush.verticalGradient(
                    listOf(selectTint.copy(alpha = 0.35f * alpha), baseBodyColor)
                )
            }
            tile.isHinted -> {
                Brush.verticalGradient(
                    listOf(Color(0xFFFFD700).copy(alpha = 0.40f * alpha), baseBodyColor)
                )
            }
            isLightFace -> {
                // High-grade silky porcelain ivory gradient
                Brush.verticalGradient(
                    listOf(
                        Color(0xFFFFFDF8).copy(alpha = alpha),
                        Color(0xFFF3EDE2).copy(alpha = alpha)
                    )
                )
            }
            else -> {
                // Polished dark cyber obsidian
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF1E293B).copy(alpha = alpha),
                        Color(0xFF0B132B).copy(alpha = alpha)
                    )
                )
            }
        }

        drawScope.drawRoundRect(
            brush = topFaceBrush,
            topLeft = Offset(x, y),
            size = Size(faceW, faceH),
            cornerRadius = CornerRadius(cr - 1f, cr - 1f)
        )

        // 4. Glossy Specular Highlight on top & left edge
        if (isLightFace) {
            // Pristine porcelain edge gloss
            drawScope.drawLine(
                color = Color.White.copy(alpha = 0.95f * alpha),
                start = Offset(x + cr, y + 1.2f),
                end = Offset(x + faceW - cr, y + 1.2f),
                strokeWidth = 1.2f
            )
            drawScope.drawLine(
                color = Color.White.copy(alpha = 0.85f * alpha),
                start = Offset(x + 1.2f, y + cr),
                end = Offset(x + 1.2f, y + faceH - cr),
                strokeWidth = 1.2f
            )
        } else {
            // Cyber specular glow
            drawScope.drawLine(
                color = primaryAccent.copy(alpha = 0.80f * alpha),
                start = Offset(x + cr, y + 1.2f),
                end = Offset(x + faceW - cr, y + 1.2f),
                strokeWidth = 1.2f
            )
        }

        // 5. Delicate Engraved Inset Inner Border (Traditional Mahjong craftsmanship)
        val innerMargin = (w * 0.055f).coerceIn(2.5f, 4f)
        val innerRimColor = if (isLightFace) {
            Color(0xFFD3C5AB).copy(alpha = 0.65f * alpha)
        } else {
            primaryAccent.copy(alpha = 0.25f * alpha)
        }
        drawScope.drawRoundRect(
            color = innerRimColor,
            topLeft = Offset(x + innerMargin, y + innerMargin),
            size = Size(faceW - innerMargin * 2f, faceH - innerMargin * 2f),
            cornerRadius = CornerRadius((cr - 2f).coerceAtLeast(2f), (cr - 2f).coerceAtLeast(2f)),
            style = Stroke(width = 0.85f)
        )

        // 6. Glow Outline for Selection or Hint
        if (tile.isSelected || tile.isHinted) {
            val glowColor = if (tile.isSelected) {
                if (isDarkMode) Color(0xFF00F0FF) else Color(0xFF0284C7)
            } else {
                Color(0xFFFFD700)
            }
            drawScope.drawRoundRect(
                color = glowColor,
                topLeft = Offset(x - 1f, y - 1f),
                size = Size(faceW + 2f, faceH + 2f),
                cornerRadius = CornerRadius(cr, cr),
                style = Stroke(width = 2.4f)
            )
            // Outer halo
            drawScope.drawRoundRect(
                color = glowColor.copy(alpha = 0.35f * alpha),
                topLeft = Offset(x - 3f, y - 3f),
                size = Size(faceW + 6f, faceH + 6f),
                cornerRadius = CornerRadius(cr + 2f, cr + 2f),
                style = Stroke(width = 1.5f)
            )
        }

        // 7. Authentic Traditional Mahjong Face Artwork
        drawAuthenticFace2D(
            drawScope = drawScope,
            tile = tile,
            x = x,
            y = y,
            w = faceW,
            h = faceH,
            isLightFace = isLightFace,
            alpha = alpha,
            isPlayable = projected.isPlayable,
            textMeasurer = textMeasurer
        )

        // 8. Blocked / Unplayable subtle dimming overlay (ensures playable tiles pop)
        if (!projected.isPlayable && !tile.isSelected) {
            val dimAlpha = if (isLightFace) 0.30f * alpha else 0.40f * alpha
            drawScope.drawRoundRect(
                color = Color(0xFF0F172A).copy(alpha = dimAlpha),
                topLeft = Offset(x, y),
                size = Size(faceW, faceH),
                cornerRadius = CornerRadius(cr - 1f, cr - 1f)
            )
        }
    }

    private fun drawAuthenticFace2D(
        drawScope: DrawScope,
        tile: MahjongTile,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        isLightFace: Boolean,
        alpha: Float,
        isPlayable: Boolean,
        textMeasurer: TextMeasurer
    ) {
        val category = tile.identity.category
        val value = tile.identity.value

        // Authentic lacquer ink colors
        val inkDark = if (isLightFace) Color(0xFF0F172A).copy(alpha = alpha) else Color(0xFF00F0FF).copy(alpha = alpha)
        val inkRed = if (isLightFace) Color(0xFFC02626).copy(alpha = alpha) else Color(0xFFFF0055).copy(alpha = alpha)
        val inkGreen = if (isLightFace) Color(0xFF15803D).copy(alpha = alpha) else Color(0xFF00FF66).copy(alpha = alpha)
        val inkBlue = if (isLightFace) Color(0xFF1D4ED8).copy(alpha = alpha) else Color(0xFF38BDF8).copy(alpha = alpha)

        val muter = if (!isPlayable) 0.85f else 1.0f

        when (category) {
            TileCategory.CHARACTERS -> {
                val numKanji = listOf("一", "二", "三", "四", "五", "六", "七", "八", "九").getOrElse(value - 1) { "$value" }

                // Top Chinese numeral in bold traditional calligraphy
                val numStyle = TextStyle(
                    color = inkDark.copy(alpha = inkDark.alpha * muter),
                    fontSize = (w * 0.36f).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                val measuredNum = textMeasurer.measure(numKanji, style = numStyle)
                drawScope.drawText(
                    textLayoutResult = measuredNum,
                    topLeft = Offset(x + (w - measuredNum.size.width) / 2f, y + h * 0.12f)
                )

                // Bottom 萬 (Wan) in authentic vermilion red
                val wanStyle = TextStyle(
                    color = inkRed.copy(alpha = inkRed.alpha * muter),
                    fontSize = (w * 0.36f).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                val measuredWan = textMeasurer.measure("萬", style = wanStyle)
                drawScope.drawText(
                    textLayoutResult = measuredWan,
                    topLeft = Offset(x + (w - measuredWan.size.width) / 2f, y + h * 0.49f)
                )

                drawCornerBadge(drawScope, textMeasurer, "$value", inkDark.copy(alpha = 0.8f * alpha * muter), x, y, w)
            }

            TileCategory.DOTS -> {
                drawAuthenticDots2D(drawScope, x, y, w, h, value, inkRed, inkGreen, inkBlue, alpha * muter)
                drawCornerBadge(drawScope, textMeasurer, "$value", inkBlue.copy(alpha = 0.8f * alpha * muter), x, y, w)
            }

            TileCategory.BAMBOO -> {
                if (value == 1) {
                    drawAuthenticBambooSparrow2D(drawScope, x, y, w, h, inkGreen, inkRed, inkBlue, alpha * muter)
                } else {
                    drawAuthenticBambooStalks2D(drawScope, x, y, w, h, value, inkGreen, inkRed, inkBlue, alpha * muter)
                }
                drawCornerBadge(drawScope, textMeasurer, "$value", inkGreen.copy(alpha = 0.8f * alpha * muter), x, y, w)
            }

            TileCategory.WINDS -> {
                val windKanji = listOf("東", "南", "西", "北").getOrElse(value - 1) { "風" }
                val windLetters = listOf("E", "S", "W", "N").getOrElse(value - 1) { "W" }
                val windColor = if (value == 1 || value == 4) inkDark else inkRed

                val style = TextStyle(
                    color = windColor.copy(alpha = windColor.alpha * muter),
                    fontSize = (w * 0.46f).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                val measured = textMeasurer.measure(windKanji, style = style)
                drawScope.drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(x + (w - measured.size.width) / 2f, y + (h - measured.size.height) / 2f)
                )
                drawCornerBadge(drawScope, textMeasurer, windLetters, windColor.copy(alpha = 0.8f * alpha * muter), x, y, w)
            }

            TileCategory.DRAGONS -> {
                when (value) {
                    1 -> {
                        // Red Dragon (中)
                        val style = TextStyle(
                            color = inkRed.copy(alpha = inkRed.alpha * muter),
                            fontSize = (w * 0.48f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif
                        )
                        val measured = textMeasurer.measure("中", style = style)
                        drawScope.drawText(
                            textLayoutResult = measured,
                            topLeft = Offset(x + (w - measured.size.width) / 2f, y + (h - measured.size.height) / 2f)
                        )
                        drawCornerBadge(drawScope, textMeasurer, "C", inkRed.copy(alpha = 0.8f * alpha * muter), x, y, w)
                    }
                    2 -> {
                        // Green Dragon (發)
                        val style = TextStyle(
                            color = inkGreen.copy(alpha = inkGreen.alpha * muter),
                            fontSize = (w * 0.48f).sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Serif
                        )
                        val measured = textMeasurer.measure("發", style = style)
                        drawScope.drawText(
                            textLayoutResult = measured,
                            topLeft = Offset(x + (w - measured.size.width) / 2f, y + (h - measured.size.height) / 2f)
                        )
                        drawCornerBadge(drawScope, textMeasurer, "F", inkGreen.copy(alpha = 0.8f * alpha * muter), x, y, w)
                    }
                    else -> {
                        // White Dragon (白) - Authentic Traditional Ornate Picture Frame
                        val frameW = w * 0.64f
                        val frameH = h * 0.70f
                        val fx = x + (w - frameW) / 2f
                        val fy = y + (h - frameH) / 2f

                        // Outer rectangular frame
                        drawScope.drawRoundRect(
                            color = inkBlue.copy(alpha = inkBlue.alpha * muter),
                            topLeft = Offset(fx, fy),
                            size = Size(frameW, frameH),
                            cornerRadius = CornerRadius(4f, 4f),
                            style = Stroke(width = 2.4f)
                        )

                        // Inner rectangular frame
                        val inset = 3.8f
                        drawScope.drawRoundRect(
                            color = inkBlue.copy(alpha = 0.75f * inkBlue.alpha * muter),
                            topLeft = Offset(fx + inset, fy + inset),
                            size = Size(frameW - inset * 2f, frameH - inset * 2f),
                            cornerRadius = CornerRadius(2.5f, 2.5f),
                            style = Stroke(width = 1.2f)
                        )

                        // Corner step brackets
                        val bracketLen = frameW * 0.20f
                        drawScope.drawLine(inkBlue, Offset(fx + inset, fy + inset + bracketLen), Offset(fx + inset + bracketLen, fy + inset), 1.2f)
                        drawScope.drawLine(inkBlue, Offset(fx + frameW - inset - bracketLen, fy + inset), Offset(fx + frameW - inset, fy + inset + bracketLen), 1.2f)
                        drawScope.drawLine(inkBlue, Offset(fx + inset, fy + frameH - inset - bracketLen), Offset(fx + inset + bracketLen, fy + frameH - inset), 1.2f)
                        drawScope.drawLine(inkBlue, Offset(fx + frameW - inset - bracketLen, fy + frameH - inset), Offset(fx + frameW - inset, fy + frameH - inset - bracketLen), 1.2f)

                        drawCornerBadge(drawScope, textMeasurer, "B", inkBlue.copy(alpha = 0.8f * alpha * muter), x, y, w)
                    }
                }
            }

            TileCategory.FLOWERS, TileCategory.SEASONS -> {
                val isFlower = category == TileCategory.FLOWERS
                val kanji = if (isFlower) {
                    listOf("梅", "蘭", "竹", "菊").getOrElse(value - 1) { "花" }
                } else {
                    listOf("春", "夏", "秋", "冬").getOrElse(value - 1) { "季" }
                }

                // Vector emblem on top half
                val emblemColor = if (isFlower) inkRed else inkBlue
                drawFlowerOrSeasonEmblem(drawScope, x, y, w, h, isFlower, value, emblemColor, inkGreen, alpha * muter)

                // Chinese calligraphy below
                val kanjiStyle = TextStyle(
                    color = inkDark.copy(alpha = inkDark.alpha * muter),
                    fontSize = (w * 0.32f).sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                val measuredKanji = textMeasurer.measure(kanji, style = kanjiStyle)
                drawScope.drawText(
                    textLayoutResult = measuredKanji,
                    topLeft = Offset(x + (w - measuredKanji.size.width) / 2f, y + h * 0.52f)
                )

                val badge = if (isFlower) "F$value" else "S$value"
                drawCornerBadge(drawScope, textMeasurer, badge, emblemColor.copy(alpha = 0.8f * alpha * muter), x, y, w)
            }
        }
    }

    private fun drawCornerBadge(
        drawScope: DrawScope,
        textMeasurer: TextMeasurer,
        text: String,
        color: Color,
        x: Float,
        y: Float,
        w: Float
    ) {
        val style = TextStyle(
            color = color,
            fontSize = (w * 0.15f).sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif
        )
        val measured = textMeasurer.measure(text, style = style)
        drawScope.drawText(
            textLayoutResult = measured,
            topLeft = Offset(x + w * 0.08f, y + w * 0.06f)
        )
    }

    private fun drawAuthenticDots2D(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        value: Int,
        red: Color,
        green: Color,
        blue: Color,
        alpha: Float
    ) {
        val cx = x + w / 2f
        val cy = y + h / 2f

        if (value == 1) {
            // Master 1-Dot Ornate Floral Rosette
            val rOuter = w * 0.33f
            val rCenter = rOuter * 0.44f

            // Outer scalloped ring
            drawScope.drawCircle(color = green, radius = rOuter, center = Offset(cx, cy), style = Stroke(width = 2.8f))
            val numPetals = 8
            for (p in 0 until numPetals) {
                val angle = (p * (2.0 * PI / numPetals)).toFloat()
                val px = cx + cos(angle) * (rOuter * 0.76f)
                val py = cy + sin(angle) * (rOuter * 0.76f)
                drawScope.drawCircle(color = blue, radius = rOuter * 0.17f, center = Offset(px, py))
                drawScope.drawCircle(color = Color.White.copy(alpha = 0.9f * alpha), radius = rOuter * 0.08f, center = Offset(px, py))
            }
            // Inner ornate ruby center with gloss reflection
            drawScope.drawCircle(color = red, radius = rCenter, center = Offset(cx, cy))
            drawScope.drawCircle(color = Color.White.copy(alpha = 0.9f * alpha), radius = rCenter * 0.32f, center = Offset(cx - rCenter * 0.2f, cy - rCenter * 0.2f))
            return
        }

        val r = w * 0.105f
        val dotPositions = ArrayList<Pair<Offset, Color>>()

        when (value) {
            2 -> {
                dotPositions.add(Pair(Offset(cx, cy - h * 0.23f), green))
                dotPositions.add(Pair(Offset(cx, cy + h * 0.23f), blue))
            }
            3 -> {
                dotPositions.add(Pair(Offset(cx - w * 0.22f, cy - h * 0.24f), blue))
                dotPositions.add(Pair(Offset(cx, cy), red))
                dotPositions.add(Pair(Offset(cx + w * 0.22f, cy + h * 0.24f), green))
            }
            4 -> {
                dotPositions.add(Pair(Offset(cx - w * 0.20f, cy - h * 0.20f), blue))
                dotPositions.add(Pair(Offset(cx + w * 0.20f, cy - h * 0.20f), green))
                dotPositions.add(Pair(Offset(cx - w * 0.20f, cy + h * 0.20f), green))
                dotPositions.add(Pair(Offset(cx + w * 0.20f, cy + h * 0.20f), blue))
            }
            5 -> {
                dotPositions.add(Pair(Offset(cx - w * 0.24f, cy - h * 0.24f), blue))
                dotPositions.add(Pair(Offset(cx + w * 0.24f, cy - h * 0.24f), green))
                dotPositions.add(Pair(Offset(cx, cy), red))
                dotPositions.add(Pair(Offset(cx - w * 0.24f, cy + h * 0.24f), green))
                dotPositions.add(Pair(Offset(cx + w * 0.24f, cy + h * 0.24f), blue))
            }
            6 -> {
                dotPositions.add(Pair(Offset(cx - w * 0.20f, cy - h * 0.24f), green))
                dotPositions.add(Pair(Offset(cx + w * 0.20f, cy - h * 0.24f), green))
                dotPositions.add(Pair(Offset(cx - w * 0.20f, cy), red))
                dotPositions.add(Pair(Offset(cx + w * 0.20f, cy), red))
                dotPositions.add(Pair(Offset(cx - w * 0.20f, cy + h * 0.24f), red))
                dotPositions.add(Pair(Offset(cx + w * 0.20f, cy + h * 0.24f), red))
            }
            7 -> {
                dotPositions.add(Pair(Offset(cx - w * 0.24f, cy - h * 0.28f), green))
                dotPositions.add(Pair(Offset(cx, cy - h * 0.20f), green))
                dotPositions.add(Pair(Offset(cx + w * 0.24f, cy - h * 0.12f), green))
                dotPositions.add(Pair(Offset(cx - w * 0.20f, cy + h * 0.08f), red))
                dotPositions.add(Pair(Offset(cx + w * 0.20f, cy + h * 0.08f), red))
                dotPositions.add(Pair(Offset(cx - w * 0.20f, cy + h * 0.26f), red))
                dotPositions.add(Pair(Offset(cx + w * 0.20f, cy + h * 0.26f), red))
            }
            8 -> {
                for (row in 0 until 4) {
                    val dy = (row - 1.5f) * (h * 0.16f)
                    dotPositions.add(Pair(Offset(cx - w * 0.20f, cy + dy), blue))
                    dotPositions.add(Pair(Offset(cx + w * 0.20f, cy + dy), blue))
                }
            }
            9 -> {
                for (row in 0 until 3) {
                    val dy = (row - 1f) * (h * 0.22f)
                    val colColor = when (row) {
                        0 -> blue
                        1 -> red
                        else -> green
                    }
                    dotPositions.add(Pair(Offset(cx - w * 0.24f, cy + dy), colColor))
                    dotPositions.add(Pair(Offset(cx, cy + dy), colColor))
                    dotPositions.add(Pair(Offset(cx + w * 0.24f, cy + dy), colColor))
                }
            }
        }

        // Draw double-ring concentric enamel dots
        for (dp in dotPositions) {
            val pt = dp.first
            val col = dp.second
            drawScope.drawCircle(color = col, radius = r, center = pt)
            drawScope.drawCircle(color = Color.White.copy(alpha = 0.92f * alpha), radius = r * 0.58f, center = pt, style = Stroke(1.1f))
            drawScope.drawCircle(color = col, radius = r * 0.28f, center = pt)
        }
    }

    private fun drawAuthenticBambooSparrow2D(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        green: Color,
        red: Color,
        blue: Color,
        alpha: Float
    ) {
        val cx = x + w / 2f
        val cy = y + h / 2f

        // Perched Bamboo twig
        val branchY = cy + h * 0.18f
        drawScope.drawLine(
            color = green,
            start = Offset(x + w * 0.18f, branchY),
            end = Offset(x + w * 0.82f, branchY),
            strokeWidth = 3f
        )
        // Twig node
        drawScope.drawCircle(color = green, radius = 2.5f, center = Offset(cx, branchY))

        // Vector Bird Body (Sparrow/Peacock)
        val bodyPath = Path().apply {
            moveTo(cx - w * 0.18f, cy + h * 0.10f)
            cubicTo(
                cx - w * 0.28f, cy - h * 0.05f,
                cx - w * 0.10f, cy - h * 0.20f,
                cx + w * 0.08f, cy - h * 0.18f
            )
            cubicTo(
                cx + w * 0.22f, cy - h * 0.12f,
                cx + w * 0.20f, cy + h * 0.05f,
                cx + w * 0.06f, cy + h * 0.12f
            )
            close()
        }
        drawScope.drawPath(bodyPath, color = green)

        // Cobalt blue breast feathers
        val breastPath = Path().apply {
            moveTo(cx - w * 0.12f, cy + h * 0.08f)
            cubicTo(
                cx - w * 0.20f, cy - h * 0.02f,
                cx - w * 0.06f, cy - h * 0.12f,
                cx + w * 0.04f, cy - h * 0.10f
            )
            close()
        }
        drawScope.drawPath(breastPath, color = blue)

        // Bird head & crest
        val headCenter = Offset(cx + w * 0.12f, cy - h * 0.20f)
        drawScope.drawCircle(color = green, radius = w * 0.11f, center = headCenter)
        // Red crest feather
        drawScope.drawCircle(color = red, radius = w * 0.045f, center = Offset(headCenter.x + 2f, headCenter.y - w * 0.12f))
        // Beak
        val beakPath = Path().apply {
            moveTo(headCenter.x + w * 0.09f, headCenter.y - 1f)
            lineTo(headCenter.x + w * 0.19f, headCenter.y + 1f)
            lineTo(headCenter.x + w * 0.09f, headCenter.y + 3f)
            close()
        }
        drawScope.drawPath(beakPath, color = red)
        // Eye
        drawScope.drawCircle(color = Color.White.copy(alpha = alpha), radius = 2.5f, center = Offset(headCenter.x + 2f, headCenter.y - 1f))
        drawScope.drawCircle(color = Color.Black, radius = 1.2f, center = Offset(headCenter.x + 2f, headCenter.y - 1f))

        // Tail feathers (Curling down behind twig)
        val tailPath = Path().apply {
            moveTo(cx - w * 0.15f, branchY - 2f)
            cubicTo(
                cx - w * 0.28f, branchY + h * 0.10f,
                cx - w * 0.22f, branchY + h * 0.22f,
                cx - w * 0.10f, branchY + h * 0.18f
            )
            close()
        }
        drawScope.drawPath(tailPath, color = blue)
    }

    private fun drawAuthenticBambooStalks2D(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        value: Int,
        green: Color,
        red: Color,
        blue: Color,
        alpha: Float
    ) {
        val cx = x + w / 2f
        val cy = y + h / 2f

        val stalkW = w * 0.105f
        val stalkH = h * 0.23f

        fun drawBambooSegment(center: Offset, color: Color) {
            val sx = center.x - stalkW / 2f
            val sy = center.y - stalkH / 2f
            val halfH = stalkH / 2f - 1f

            // Top segment
            drawScope.drawRoundRect(
                color = color,
                topLeft = Offset(sx, sy),
                size = Size(stalkW, halfH),
                cornerRadius = CornerRadius(2.5f, 2.5f)
            )
            // Bottom segment
            drawScope.drawRoundRect(
                color = color,
                topLeft = Offset(sx, sy + halfH + 2f),
                size = Size(stalkW, halfH),
                cornerRadius = CornerRadius(2.5f, 2.5f)
            )
            // Node line
            drawScope.drawLine(
                color = Color.White.copy(alpha = 0.90f * alpha),
                start = Offset(sx - 1f, sy + halfH + 1f),
                end = Offset(sx + stalkW + 1f, sy + halfH + 1f),
                strokeWidth = 1.2f
            )
            // Left specular volume highlight
            drawScope.drawLine(
                color = Color.White.copy(alpha = 0.50f * alpha),
                start = Offset(sx + 1.2f, sy + 1.5f),
                end = Offset(sx + 1.2f, sy + stalkH - 1.5f),
                strokeWidth = 0.8f
            )
        }

        when (value) {
            2 -> {
                drawBambooSegment(Offset(cx, cy - h * 0.18f), green)
                drawBambooSegment(Offset(cx, cy + h * 0.18f), green)
            }
            3 -> {
                drawBambooSegment(Offset(cx, cy - h * 0.22f), blue)
                drawBambooSegment(Offset(cx - w * 0.18f, cy + h * 0.18f), green)
                drawBambooSegment(Offset(cx + w * 0.18f, cy + h * 0.18f), green)
            }
            4 -> {
                drawBambooSegment(Offset(cx - w * 0.18f, cy - h * 0.18f), blue)
                drawBambooSegment(Offset(cx + w * 0.18f, cy - h * 0.18f), green)
                drawBambooSegment(Offset(cx - w * 0.18f, cy + h * 0.18f), green)
                drawBambooSegment(Offset(cx + w * 0.18f, cy + h * 0.18f), blue)
            }
            5 -> {
                drawBambooSegment(Offset(cx - w * 0.22f, cy - h * 0.22f), blue)
                drawBambooSegment(Offset(cx + w * 0.22f, cy - h * 0.22f), green)
                drawBambooSegment(Offset(cx, cy), red)
                drawBambooSegment(Offset(cx - w * 0.22f, cy + h * 0.22f), green)
                drawBambooSegment(Offset(cx + w * 0.22f, cy + h * 0.22f), blue)
            }
            6 -> {
                for (col in -1..1 step 2) {
                    val px = cx + col * (w * 0.18f)
                    drawBambooSegment(Offset(px, cy - h * 0.22f), green)
                    drawBambooSegment(Offset(px, cy), green)
                    drawBambooSegment(Offset(px, cy + h * 0.22f), green)
                }
            }
            7 -> {
                drawBambooSegment(Offset(cx, cy - h * 0.26f), red)
                for (col in -1..1 step 2) {
                    val px = cx + col * (w * 0.20f)
                    drawBambooSegment(Offset(px, cy - h * 0.05f), green)
                    drawBambooSegment(Offset(px, cy + h * 0.14f), blue)
                    drawBambooSegment(Offset(px, cy + h * 0.30f), green)
                }
            }
            8 -> {
                for (col in -1..1 step 2) {
                    val px = cx + col * (w * 0.20f)
                    for (row in 0 until 4) {
                        val py = cy + (row - 1.5f) * (h * 0.16f)
                        drawBambooSegment(Offset(px, py), if (row % 2 == 0) blue else green)
                    }
                }
            }
            9 -> {
                for (row in 0 until 3) {
                    val py = cy + (row - 1f) * (h * 0.22f)
                    val rowCol = when (row) {
                        0 -> blue
                        1 -> red
                        else -> green
                    }
                    drawBambooSegment(Offset(cx - w * 0.24f, py), rowCol)
                    drawBambooSegment(Offset(cx, py), rowCol)
                    drawBambooSegment(Offset(cx + w * 0.24f, py), rowCol)
                }
            }
        }
    }

    private fun drawFlowerOrSeasonEmblem(
        drawScope: DrawScope,
        x: Float,
        y: Float,
        w: Float,
        h: Float,
        isFlower: Boolean,
        value: Int,
        color: Color,
        green: Color,
        alpha: Float
    ) {
        val cx = x + w / 2f
        val cy = y + h * 0.26f
        val r = w * 0.15f

        if (isFlower) {
            // Plum / Orchid / Chrysanthemum blossom
            val numPetals = if (value == 4) 8 else 5
            for (i in 0 until numPetals) {
                val angle = (i * (2.0 * PI / numPetals)).toFloat()
                val px = cx + cos(angle) * (r * 0.65f)
                val py = cy + sin(angle) * (r * 0.65f)
                drawScope.drawCircle(color = color, radius = r * 0.45f, center = Offset(px, py))
            }
            // Golden blossom center
            drawScope.drawCircle(color = Color(0xFFFFD700), radius = r * 0.32f, center = Offset(cx, cy))
        } else {
            when (value) {
                1 -> {
                    // Spring sprout
                    drawScope.drawCircle(color = green, radius = r * 0.45f, center = Offset(cx - r * 0.3f, cy - r * 0.2f))
                    drawScope.drawCircle(color = green, radius = r * 0.45f, center = Offset(cx + r * 0.3f, cy - r * 0.2f))
                    drawScope.drawLine(green, Offset(cx, cy + r * 0.6f), Offset(cx, cy - r * 0.1f), 2.5f)
                }
                2 -> {
                    // Summer sun
                    drawScope.drawCircle(color = color, radius = r * 0.55f, center = Offset(cx, cy))
                    drawScope.drawCircle(color = Color(0xFFFFD700), radius = r * 0.30f, center = Offset(cx, cy))
                }
                3 -> {
                    // Autumn leaf
                    drawScope.drawCircle(color = Color(0xFFD97706), radius = r * 0.55f, center = Offset(cx, cy))
                    drawScope.drawLine(Color(0xFF92400E), Offset(cx, cy + r * 0.6f), Offset(cx, cy - r * 0.4f), 2f)
                }
                else -> {
                    // Winter snowflake
                    drawScope.drawLine(color, Offset(cx - r * 0.6f, cy), Offset(cx + r * 0.6f, cy), 2f)
                    drawScope.drawLine(color, Offset(cx, cy - r * 0.6f), Offset(cx, cy + r * 0.6f), 2f)
                    drawScope.drawLine(color, Offset(cx - r * 0.45f, cy - r * 0.45f), Offset(cx + r * 0.45f, cy + r * 0.45f), 2f)
                    drawScope.drawLine(color, Offset(cx - r * 0.45f, cy + r * 0.45f), Offset(cx + r * 0.45f, cy - r * 0.45f), 2f)
                }
            }
        }
    }
}

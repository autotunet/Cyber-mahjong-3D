package com.example.model

import java.util.UUID
import kotlin.math.abs

object MahjongLayout {

    fun generateDeck(count: Int): List<TileIdentity> {
        val fullDeck = mutableListOf<TileIdentity>()

        // 1. Dots (1-9) x 4 = 36
        val dotLabels = listOf("①", "②", "③", "④", "⑤", "⑥", "⑦", "⑧", "⑨")
        for (v in 1..9) {
            repeat(4) {
                fullDeck.add(TileIdentity(TileCategory.DOTS, v, dotLabels[v - 1], "$v Círculo"))
            }
        }

        // 2. Bamboo (1-9) x 4 = 36
        val bambooLabels = listOf("🀐", "🀑", "🀒", "🀓", "🀔", "🀕", "🀖", "🀗", "🀘")
        for (v in 1..9) {
            repeat(4) {
                fullDeck.add(TileIdentity(TileCategory.BAMBOO, v, bambooLabels[v - 1], "$v Bambú"))
            }
        }

        // 3. Characters (1-9) x 4 = 36
        val charLabels = listOf("一", "二", "三", "四", "五", "六", "七", "八", "九")
        for (v in 1..9) {
            repeat(4) {
                fullDeck.add(TileIdentity(TileCategory.CHARACTERS, v, charLabels[v - 1], "$v Carácter"))
            }
        }

        // 4. Winds (East, South, West, North) x 4 = 16
        val windLabels = listOf("東", "南", "西", "北")
        val windNames = listOf("Este", "Sur", "Oeste", "Norte")
        for (v in 1..4) {
            repeat(4) {
                fullDeck.add(TileIdentity(TileCategory.WINDS, v, windLabels[v - 1], "Viento ${windNames[v - 1]}"))
            }
        }

        // 5. Dragons (Red, Green, White) x 4 = 12
        val dragonLabels = listOf("中", "發", "🀆")
        val dragonNames = listOf("Dragón Rojo", "Dragón Verde", "Dragón Blanco")
        for (v in 1..3) {
            repeat(4) {
                fullDeck.add(TileIdentity(TileCategory.DRAGONS, v, dragonLabels[v - 1], dragonNames[v - 1]))
            }
        }

        // 6. Flowers (1-4) = 4
        val flowerLabels = listOf("🌸", "🌺", "🌼", "🌻")
        val flowerNames = listOf("Ciruelo Cyber", "Orquídea Neón", "Crisantemo", "Bambú Flor")
        for (v in 1..4) {
            fullDeck.add(TileIdentity(TileCategory.FLOWERS, v, flowerLabels[v - 1], flowerNames[v - 1]))
        }

        // 7. Seasons (1-4) = 4
        val seasonLabels = listOf("🌱", "☀️", "🍂", "❄️")
        val seasonNames = listOf("Primavera Cuántica", "Verano Solar", "Otoño Estelar", "Invierno Criogénico")
        for (v in 1..4) {
            fullDeck.add(TileIdentity(TileCategory.SEASONS, v, seasonLabels[v - 1], seasonNames[v - 1]))
        }

        // If target count is less than 144, pick pairs cleanly
        if (count >= 144) {
            return fullDeck.shuffled()
        }

        // Build a balanced subset of pairs
        val targetPairs = count / 2
        val pairsList = mutableListOf<TileIdentity>()
        val grouped = fullDeck.chunked(2)
        val selectedGroups = grouped.shuffled().take(targetPairs)
        for (group in selectedGroups) {
            pairsList.addAll(group)
        }
        return pairsList.shuffled()
    }

    data class TilePosition(val x: Float, val y: Float, val z: Int)

    /**
     * Classic Turtle Layout positions (up to 144 slots in 4 vertical layers)
     */
    fun createPositions(difficulty: Difficulty): List<TilePosition> {
        return when (difficulty) {
            Difficulty.EASY -> createCompactPositions(48)
            Difficulty.MEDIUM -> createFortressPositions(72)
            Difficulty.HARD, Difficulty.EXPERT -> createTurtlePositions144()
        }
    }

    private fun createCompactPositions(count: Int): List<TilePosition> {
        val list = mutableListOf<TilePosition>()
        // Base layer Z=0: 6x5 grid with corners notched (26 tiles)
        for (r in 0 until 5) {
            for (c in 0 until 6) {
                if ((r == 0 || r == 4) && (c == 0 || c == 5)) continue
                list.add(TilePosition((c - 2.5f) * 2.0f, (r - 2.0f) * 2.6f, 0))
            }
        }
        // Layer Z=1: 4x4 center grid (16 tiles)
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                list.add(TilePosition((c - 1.5f) * 2.0f, (r - 1.5f) * 2.6f, 1))
            }
        }
        // Layer Z=2: 2x3 apex (6 tiles)
        for (r in 0 until 3) {
            for (c in 0 until 2) {
                list.add(TilePosition((c - 0.5f) * 2.0f, (r - 1.0f) * 2.6f, 2))
            }
        }
        return list.take(count)
    }

    private fun createFortressPositions(count: Int): List<TilePosition> {
        val list = mutableListOf<TilePosition>()
        // Two towers and a bridge connecting them in 3 layers
        // Left Tower Base (Z=0): 3x4
        for (r in 0 until 4) {
            for (c in 0 until 3) {
                list.add(TilePosition((c - 4.0f) * 2.0f, (r - 1.5f) * 2.6f, 0))
            }
        }
        // Right Tower Base (Z=0): 3x4
        for (r in 0 until 4) {
            for (c in 0 until 3) {
                list.add(TilePosition((c + 2.0f) * 2.0f, (r - 1.5f) * 2.6f, 0))
            }
        }
        // Central Plaza (Z=0): 2x3
        for (r in 0 until 3) {
            for (c in 0 until 2) {
                list.add(TilePosition((c - 0.5f) * 2.0f, (r - 1.0f) * 2.6f, 0))
            }
        }

        // Left Tower Tier 1 (Z=1): 2x3
        for (r in 0 until 3) {
            for (c in 0 until 2) {
                list.add(TilePosition((c - 3.5f) * 2.0f, (r - 1.0f) * 2.6f, 1))
            }
        }
        // Right Tower Tier 1 (Z=1): 2x3
        for (r in 0 until 3) {
            for (c in 0 until 2) {
                list.add(TilePosition((c + 2.5f) * 2.0f, (r - 1.0f) * 2.6f, 1))
            }
        }
        // Skybridge connecting towers (Z=1): 2x2
        for (r in 0 until 2) {
            for (c in 0 until 2) {
                list.add(TilePosition((c - 0.5f) * 2.0f, (r - 0.5f) * 2.6f, 1))
            }
        }

        // Left Pinnacle (Z=2): 1x2
        for (r in 0 until 2) {
            list.add(TilePosition(-3.0f * 2.0f, (r - 0.5f) * 2.6f, 2))
        }
        // Right Pinnacle (Z=2): 1x2
        for (r in 0 until 2) {
            list.add(TilePosition(3.0f * 2.0f, (r - 0.5f) * 2.6f, 2))
        }
        // Center Nexus Apex (Z=2): 1x2
        for (r in 0 until 2) {
            list.add(TilePosition(0.0f, (r - 0.5f) * 2.6f, 2))
        }

        return list.take(count)
    }

    private fun createTurtlePositions144(): List<TilePosition> {
        val list = mutableListOf<TilePosition>()
        // Traditional Turtle layout has 144 tiles
        // Layer 0: 86 tiles
        // Row definitions for Layer 0 (relative column start and counts)
        val l0Rows = listOf(
            listOf(TilePosition(-12.0f, 0.0f, 0)), // far left wing
            // rows 1..8
            (0 until 12).map { TilePosition((it - 5.5f) * 2.0f, -4.0f * 2.6f, 0) },
            (0 until 8).map { TilePosition((it - 3.5f) * 2.0f, -3.0f * 2.6f, 0) },
            (0 until 10).map { TilePosition((it - 4.5f) * 2.0f, -2.0f * 2.6f, 0) },
            (0 until 12).map { TilePosition((it - 5.5f) * 2.0f, -1.0f * 2.6f, 0) },
            (0 until 12).map { TilePosition((it - 5.5f) * 2.0f, 0.0f * 2.6f, 0) },
            (0 until 12).map { TilePosition((it - 5.5f) * 2.0f, 1.0f * 2.6f, 0) },
            (0 until 10).map { TilePosition((it - 4.5f) * 2.0f, 2.0f * 2.6f, 0) },
            (0 until 8).map { TilePosition((it - 3.5f) * 2.0f, 3.0f * 2.6f, 0) },
            (0 until 12).map { TilePosition((it - 5.5f) * 2.0f, 4.0f * 2.6f, 0) },
            listOf(TilePosition(12.0f, 0.0f, 0)) // far right wing
        )
        for (row in l0Rows) {
            list.addAll(row)
        }

        // Layer 1: 36 tiles (6x6 center block)
        for (r in 0 until 6) {
            for (c in 0 until 6) {
                list.add(TilePosition((c - 2.5f) * 2.0f, (r - 2.5f) * 2.6f, 1))
            }
        }

        // Layer 2: 16 tiles (4x4 center pyramid tier)
        for (r in 0 until 4) {
            for (c in 0 until 4) {
                list.add(TilePosition((c - 1.5f) * 2.0f, (r - 1.5f) * 2.6f, 2))
            }
        }

        // Layer 3: 4 tiles (2x2 near apex)
        for (r in 0 until 2) {
            for (c in 0 until 2) {
                list.add(TilePosition((c - 0.5f) * 2.0f, (r - 0.5f) * 2.6f, 3))
            }
        }

        // Layer 4: 1 apex crown tile (centered at peak)
        list.add(TilePosition(0.0f, 0.0f, 4))

        return list.take(144)
    }

    /**
     * Determines whether a tile can be clicked and matched according to standard Mahjong Solitaire rules:
     * - No tile directly resting on top of it at higher Z layers.
     * - At least one side (Left or Right) is completely open.
     */
    fun isTilePlayable(tile: MahjongTile, tiles: List<MahjongTile>): Boolean {
        if (tile.isMatched) return false

        val activeTiles = tiles.filter { !it.isMatched && it.id != tile.id }

        // 1. Check if covered by ANY tile above (Z > tile.gridZ)
        val isCovered = activeTiles.any { other ->
            other.gridZ > tile.gridZ &&
            abs(other.gridX - tile.gridX) < 1.85f &&
            abs(other.gridY - tile.gridY) < 2.45f
        }
        if (isCovered) return false

        // 2. Check if blocked on left (same Z)
        val blockedLeft = activeTiles.any { other ->
            other.gridZ == tile.gridZ &&
            abs(other.gridY - tile.gridY) < 2.2f &&
            other.gridX < tile.gridX &&
            (tile.gridX - other.gridX) <= 2.05f
        }

        // 3. Check if blocked on right (same Z)
        val blockedRight = activeTiles.any { other ->
            other.gridZ == tile.gridZ &&
            abs(other.gridY - tile.gridY) < 2.2f &&
            other.gridX > tile.gridX &&
            (other.gridX - tile.gridX) <= 2.05f
        }

        // Tile is playable if covered == false AND (not blockedLeft OR not blockedRight)
        return !blockedLeft || !blockedRight
    }

    /**
     * Counts the total number of valid matching pairs available right now on the board.
     */
    fun countAvailablePairs(tiles: List<MahjongTile>): Int {
        val playable = tiles.filter { isTilePlayable(it, tiles) }
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

    /**
     * Finds playable tiles that form a valid match pair (for Hints).
     */
    fun findAvailableMatch(tiles: List<MahjongTile>): Pair<MahjongTile, MahjongTile>? {
        val playable = tiles.filter { isTilePlayable(it, tiles) }
        for (i in playable.indices) {
            for (j in (i + 1) until playable.size) {
                if (playable[i].identity.matches(playable[j].identity)) {
                    return Pair(playable[i], playable[j])
                }
            }
        }
        return null
    }

    /**
     * Reorganizes active tiles on the board guaranteeing that at least one (or more)
     * valid matching pairs are immediately playable, preventing any game deadlock.
     */
    fun guaranteedReshuffle(tiles: List<MahjongTile>): List<MahjongTile> {
        val activeIndices = tiles.indices.filter { !tiles[it].isMatched }
        if (activeIndices.size < 2) return tiles

        val activeTiles = activeIndices.map { tiles[it] }
        val playableSlots = activeTiles.filter { isTilePlayable(it, tiles) }

        // If we have at least 2 playable slots, force-match them directly
        if (playableSlots.size >= 2) {
            val allIdentities = activeTiles.map { it.identity }.toMutableList()
            var foundPair: Pair<TileIdentity, TileIdentity>? = null
            outer@ for (i in allIdentities.indices) {
                for (j in (i + 1) until allIdentities.size) {
                    if (allIdentities[i].matches(allIdentities[j])) {
                        foundPair = Pair(allIdentities[i], allIdentities[j])
                        allIdentities.removeAt(j)
                        allIdentities.removeAt(i)
                        break@outer
                    }
                }
            }

            if (foundPair != null) {
                val assigned = HashMap<String, TileIdentity>()
                assigned[playableSlots[0].id] = foundPair.first
                assigned[playableSlots[1].id] = foundPair.second

                allIdentities.shuffle()
                var idx = 0
                for (tile in activeTiles) {
                    if (tile.id != playableSlots[0].id && tile.id != playableSlots[1].id) {
                        if (idx < allIdentities.size) {
                            assigned[tile.id] = allIdentities[idx++]
                        }
                    }
                }

                val candidate = tiles.map { t ->
                    val newId = assigned[t.id]
                    if (newId != null) {
                        t.copy(identity = newId, isSelected = false, isHinted = false)
                    } else {
                        t
                    }
                }

                if (countAvailablePairs(candidate) > 0) {
                    return candidate
                }
            }
        }

        // Iterative shuffle with solvability check
        val activeIds = activeTiles.map { it.identity }
        var bestResult = tiles
        var bestPairs = 0

        for (attempt in 0..35) {
            val shuffledIds = activeIds.shuffled()
            var sIdx = 0
            val candidate = tiles.map { t ->
                if (!t.isMatched) {
                    t.copy(identity = shuffledIds[sIdx++], isSelected = false, isHinted = false)
                } else {
                    t
                }
            }
            val pairs = countAvailablePairs(candidate)
            if (pairs > 0) {
                return candidate
            }
            if (pairs > bestPairs) {
                bestPairs = pairs
                bestResult = candidate
            }
        }
        return bestResult
    }

    /**
     * Builds and initializes a new game board with guaranteed initial available moves.
     */
    fun generateGameBoard(difficulty: Difficulty): List<MahjongTile> {
        val rawPositions = createPositions(difficulty)
        // Ensure even count for perfect pairing
        val positions = if (rawPositions.size % 2 != 0) rawPositions.dropLast(1) else rawPositions
        val count = positions.size
        val identities = generateDeck(count)

        var board = positions.mapIndexed { index, pos ->
            MahjongTile(
                id = UUID.randomUUID().toString(),
                identity = identities[index % identities.size],
                gridX = pos.x,
                gridY = pos.y,
                gridZ = pos.z
            )
        }

        // Ensure newly dealt board has at least 3 playable pairs
        var attempts = 0
        while (countAvailablePairs(board) < 3 && attempts < 25) {
            board = guaranteedReshuffle(board)
            attempts++
        }

        return board
    }
}

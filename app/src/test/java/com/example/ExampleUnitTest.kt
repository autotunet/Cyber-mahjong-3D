package com.example

import com.example.model.Achievement
import com.example.model.Difficulty
import com.example.model.MahjongLayout
import com.example.model.MahjongTile
import com.example.model.TileCategory
import com.example.model.TileIdentity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testMahjongDeckAndPairGeneration() {
        val easyBoard = MahjongLayout.generateGameBoard(Difficulty.EASY)
        assertTrue(easyBoard.isNotEmpty())
        assertEquals(48, easyBoard.size)

        // Count playable tiles on fresh board
        val playable = easyBoard.filter { MahjongLayout.isTilePlayable(it, easyBoard) }
        assertTrue("There must be playable tiles at game start", playable.size >= 2)

        // Verify available matches exists
        val match = MahjongLayout.findAvailableMatch(easyBoard)
        assertNotNull("Fresh board should have at least one matchable pair", match)
    }

    @Test
    fun testTileMatchingRules() {
        val dot1A = TileIdentity(TileCategory.DOTS, 1, "①", "1 Dot")
        val dot1B = TileIdentity(TileCategory.DOTS, 1, "①", "1 Dot")
        val dot2 = TileIdentity(TileCategory.DOTS, 2, "②", "2 Dot")
        val flower1 = TileIdentity(TileCategory.FLOWERS, 1, "🌸", "Flor 1")
        val flower2 = TileIdentity(TileCategory.FLOWERS, 2, "🌺", "Flor 2")

        assertTrue(dot1A.matches(dot1B))
        assertFalse(dot1A.matches(dot2))
        // Any flower matches another flower in Mahjong Solitaire
        assertTrue(flower1.matches(flower2))
    }

    @Test
    fun testTilePlayableRules() {
        val baseTile = MahjongTile(
            id = "t1",
            identity = TileIdentity(TileCategory.CHARACTERS, 1, "一", "1"),
            gridX = 0f,
            gridY = 0f,
            gridZ = 0
        )
        val coveringTile = MahjongTile(
            id = "t2",
            identity = TileIdentity(TileCategory.CHARACTERS, 2, "二", "2"),
            gridX = 0f,
            gridY = 0f,
            gridZ = 1
        )

        // Tile with nothing covering it is free
        assertTrue(MahjongLayout.isTilePlayable(baseTile, listOf(baseTile)))

        // Covered tile is blocked
        assertFalse(MahjongLayout.isTilePlayable(baseTile, listOf(baseTile, coveringTile)))
    }

    @Test
    fun testAchievementSharingText() {
        val ach = Achievement(
            id = "FIRST_LINK",
            title = "Primer Enlace",
            description = "Empareja tu primera pareja",
            rewardNanocores = 50,
            iconName = "link"
        )
        val text = ach.generateShareText(playerLevel = 3, totalScore = 1500)
        assertTrue(text.contains("Primer Enlace"))
        assertTrue(text.contains("1500"))
        assertTrue(text.contains("#CyberMahjong"))
    }
}

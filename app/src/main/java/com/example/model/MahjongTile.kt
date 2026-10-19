package com.example.model

enum class TileCategory {
    DOTS,       // Círculos cuánticos (1-9)
    BAMBOO,     // Varillas láser (1-9)
    CHARACTERS, // Caracteres cibernéticos (1-9)
    WINDS,      // Vientos cardinales (Este, Sur, Oeste, Norte)
    DRAGONS,    // Dragones (Rojo/Chun, Verde/Fa, Blanco/Bai)
    FLOWERS,    // Flores cibernéticas
    SEASONS     // Estaciones temporales
}

data class TileIdentity(
    val category: TileCategory,
    val value: Int,             // 1-9 for suits, 1-4 for winds, 1-3 for dragons, 1-4 for flowers/seasons
    val symbolLabel: String,    // Kanji / Unicode / Graphic representation
    val secondaryLabel: String  // English / Spanish subtitle (e.g. "中 Red", "東 East", "1 Dot")
) {
    /**
     * Checks if this tile can match with another tile according to Mahjong Solitaire rules.
     * Regular tiles match on identical category and value.
     * Any flower matches any flower, and any season matches any season.
     */
    fun matches(other: TileIdentity): Boolean {
        return when {
            category == TileCategory.FLOWERS && other.category == TileCategory.FLOWERS -> true
            category == TileCategory.SEASONS && other.category == TileCategory.SEASONS -> true
            else -> category == other.category && value == other.value
        }
    }
}

data class MahjongTile(
    val id: String,
    val identity: TileIdentity,
    val gridX: Float,
    val gridY: Float,
    val gridZ: Int,
    val isMatched: Boolean = false,
    val isSelected: Boolean = false,
    val isHinted: Boolean = false,
    val dissolveProgress: Float = 0f // 0f = normal, 1f = fully dissolved into cyber dust
) {
    val width: Float = 2.0f
    val height: Float = 2.6f
    val depth: Float = 0.6f
}

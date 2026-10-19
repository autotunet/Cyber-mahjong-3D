package com.example.model

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val rewardNanocores: Int,
    val iconName: String,
    val isUnlocked: Boolean = false,
    val progress: Float = 0f // 0f to 1f
) {
    fun generateShareText(playerLevel: Int, totalScore: Int): String {
        return """
            🎮 ¡He desbloqueado el logro "$title" en Cyber Mahjong! 🚀
            
            ⚡ Descripción: $description
            🏆 Nivel de Hacker: $playerLevel | Puntuación: $totalScore pts
            🀄 Fichas auténticas y efectos especiales cibernéticos.
            
            ¿Puedes superar mi récord en Cyber Mahjong? #CyberMahjong #Cyberpunk
        """.trimIndent()
    }
}

package com.example.model

enum class Difficulty(
    val title: String,
    val description: String,
    val initialTileCount: Int,
    val timeLimitSeconds: Int?, // null for untimed / zen
    val scoreMultiplier: Float
) {
    EASY(
        title = "Principiante",
        description = "48 fichas con 2:00 min de desafío contra reloj",
        initialTileCount = 48,
        timeLimitSeconds = 120,
        scoreMultiplier = 1.0f
    ),
    MEDIUM(
        title = "Cibernético",
        description = "72 fichas en fortaleza con 3:00 min de límite",
        initialTileCount = 72,
        timeLimitSeconds = 180,
        scoreMultiplier = 1.5f
    ),
    HARD(
        title = "Hacker Neo",
        description = "Pirámide completa de 144 fichas con 5:00 min",
        initialTileCount = 144,
        timeLimitSeconds = 300,
        scoreMultiplier = 2.0f
    ),
    EXPERT(
        title = "Sobrecarga Cuántica",
        description = "Desafío élite de 144 fichas con solo 4:00 min",
        initialTileCount = 144,
        timeLimitSeconds = 240,
        scoreMultiplier = 3.0f
    );

    val label: String get() = title
}

enum class TileSkin(
    val id: String,
    val title: String,
    val description: String,
    val unlockCost: Int, // in Nanocores (0 = unlocked by default)
    val bodyColorHex: Long,
    val backingColorHex: Long,
    val bevelColorHex: Long,
    val primaryAccentHex: Long,
    val secondaryAccentHex: Long,
    val isLightFace: Boolean = false
) {
    JADE_IVORY(
        id = "jade_ivory",
        title = "Marfil & Jade Tradicional",
        description = "Fichas auténticas tradicionales: cara de marfil con reverso de bambú jade tallado",
        unlockCost = 0,
        bodyColorHex = 0xFFFCFAF4,
        backingColorHex = 0xFF14532D, // Classic deep jade bamboo backing
        bevelColorHex = 0xFFE2D8C6,
        primaryAccentHex = 0xFF166534, // Traditional deep ink green
        secondaryAccentHex = 0xFFB91C1C, // Traditional vermilion red
        isLightFace = true
    ),
    CYBERPUNK_NEON(
        id = "cyber_neon",
        title = "Neón Cyberpunk",
        description = "Obsidiana con circuitos cian y magenta luminiscentes",
        unlockCost = 0,
        bodyColorHex = 0xFF0B132B,
        backingColorHex = 0xFF03223A,
        bevelColorHex = 0xFF1C2541,
        primaryAccentHex = 0xFF00F0FF,
        secondaryAccentHex = 0xFFFF007F,
        isLightFace = false
    ),
    QUANTUM_MATRIX(
        id = "quantum_matrix",
        title = "Matriz Cuántica",
        description = "Terminal digital con glifos verde radiactivo",
        unlockCost = 350,
        bodyColorHex = 0xFF031408,
        backingColorHex = 0xFF02200D,
        bevelColorHex = 0xFF0A2B14,
        primaryAccentHex = 0xFF00FF66,
        secondaryAccentHex = 0xFF39FF14,
        isLightFace = false
    ),
    OBSIDIAN_GOLD(
        id = "obsidian_gold",
        title = "Obsidiana & Oro",
        description = "Fibra de carbono mate con grabados en pan de oro",
        unlockCost = 800,
        bodyColorHex = 0xFF121214,
        backingColorHex = 0xFF2A2008,
        bevelColorHex = 0xFF2A2A2E,
        primaryAccentHex = 0xFFFFD700,
        secondaryAccentHex = 0xFFFFA000,
        isLightFace = false
    )
}

enum class ScenarioTheme(
    val id: String,
    val title: String,
    val subtitle: String,
    val unlockCost: Int,
    val drawableResName: String,
    val ambientDroneBaseFreq: Float,
    val gridColorHex: Long,
    val atmosphericColorHex: Long
) {
    NEO_TOKYO(
        id = "neo_tokyo",
        title = "Neo Tokio Highline",
        subtitle = "Rascacielos iluminados con lluvia digital y neón",
        unlockCost = 0,
        drawableResName = "bg_neo_tokyo",
        ambientDroneBaseFreq = 110f, // Deep calming A2
        gridColorHex = 0x3300F0FF,
        atmosphericColorHex = 0x660A0E1A
    ),
    ORBITAL_STATION(
        id = "orbital_station",
        title = "Estación Orbital Zen",
        subtitle = "Vista panorámica a nebulosas en gravedad cero",
        unlockCost = 500,
        drawableResName = "bg_orbital_station",
        ambientDroneBaseFreq = 130.81f, // C3 Zen
        gridColorHex = 0x339D4EDD,
        atmosphericColorHex = 0x660B081F
    ),
    CYBER_TEMPLE(
        id = "cyber_temple",
        title = "Templo Matrix",
        subtitle = "Torii digital con corrientes de código flotante",
        unlockCost = 900,
        drawableResName = "bg_neo_tokyo",
        ambientDroneBaseFreq = 146.83f, // D3 Matrix
        gridColorHex = 0x3300FF66,
        atmosphericColorHex = 0x6603170B
    ),
    QUANTUM_VOID(
        id = "quantum_void",
        title = "Vacío Cuántico",
        subtitle = "Hiperespacio abstracto con haces de luz pura",
        unlockCost = 1500,
        drawableResName = "bg_orbital_station",
        ambientDroneBaseFreq = 164.81f, // E3 Cosmic
        gridColorHex = 0x33FF007F,
        atmosphericColorHex = 0x6618031A
    )
}

enum class GraphicsQuality(
    val title: String,
    val description: String,
    val maxParticles: Int,
    val enableBloom: Boolean,
    val enableRealtimeShadows: Boolean
) {
    LOW(
        title = "Rendimiento (Gama Baja)",
        description = "60 FPS fluidos, sin sobrecalentamiento",
        maxParticles = 8,
        enableBloom = false,
        enableRealtimeShadows = false
    ),
    BALANCED(
        title = "Equilibrado",
        description = "Gráficos atractivos con buen rendimiento",
        maxParticles = 24,
        enableBloom = true,
        enableRealtimeShadows = true
    ),
    ULTRA_CYBER(
        title = "Ultra Cyber",
        description = "Máximos efectos de partículas, reflejos y luz",
        maxParticles = 60,
        enableBloom = true,
        enableRealtimeShadows = true
    );

    val label: String get() = title
}

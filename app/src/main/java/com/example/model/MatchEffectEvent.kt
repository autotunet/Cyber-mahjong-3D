package com.example.model

data class MatchEffectEvent(
    val eventId: Long = System.currentTimeMillis(),
    val tile1Id: String,
    val tile2Id: String,
    val gridX1: Float,
    val gridY1: Float,
    val gridZ1: Int,
    val gridX2: Float,
    val gridY2: Float,
    val gridZ2: Int,
    val points: Int,
    val combo: Int
)

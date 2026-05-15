package com.nammaraste.health.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class Route(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,             // e.g., "Kolar Heritage Circuit"
    val startLocation: String,    // "Malur"
    val endLocation: String,      // "Kolar"
    val totalDistanceKm: Double,
    val description: String,
    val difficultyLevel: String,  // "Easy", "Moderate", "Challenging"
    val isActive: Boolean = true
)

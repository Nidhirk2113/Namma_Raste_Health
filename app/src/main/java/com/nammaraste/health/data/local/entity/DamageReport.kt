package com.nammaraste.health.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "damage_reports")
data class DamageReport(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val roadId: Int,
    val damageType: String,
    val description: String,
    val photoPath: String,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val isResolved: Boolean = false    // ← ADD THIS LINE
)
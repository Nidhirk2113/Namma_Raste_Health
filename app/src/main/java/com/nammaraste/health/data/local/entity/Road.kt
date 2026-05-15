package com.nammaraste.health.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "roads")
data class Road(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val roadCode: String,
    val name: String,
    val lengthKm: Double,
    val district: String,        // area from CSV
    val taluka: String,          // road_type from CSV
    val hobli: String,           // surface_type from CSV
    val pincode: String,
    val scheme: String,
    val constructionYear: Int,
    val warrantyEnd: String,
    val contractorName: String,
    val contractorLicense: String,
    val contractorPhone: String,
    val baseHealthScore: Int = 100,
    val isActive: Boolean = true,

    // ── Fields from Roads_data.csv ──
    val potholeCount: Int = 0,
    val avgTrafficPerDay: Int = 0,
    val conditionStatus: String = "",
    val lastInspectionDate: String = "",
    val maintenancePriority: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val estimatedRepairCostInr: Long = 0L,
    val reportedByUsers: Int = 0,
    val streetlightAvailability: String = "",
    val drainageCondition: String = "",
    val accidentRiskScore: Double = 0.0
)

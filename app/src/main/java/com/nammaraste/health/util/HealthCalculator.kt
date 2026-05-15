package com.nammaraste.health.util

import com.nammaraste.health.R

object HealthCalculator {

    // Score drops by 3 per damage report. Floor is 5.
    fun calculateScore(baseScore: Int, reportCount: Int): Int {
        return maxOf(5, baseScore - (reportCount * 3))
    }

    fun getStatus(score: Int): HealthStatus = when {
        score >= 70 -> HealthStatus.GOOD
        score >= 40 -> HealthStatus.AT_RISK
        else        -> HealthStatus.CRITICAL
    }

    fun getStatusLabel(score: Int): String = when (getStatus(score)) {
        HealthStatus.GOOD     -> "Good"
        HealthStatus.AT_RISK  -> "At Risk"
        HealthStatus.CRITICAL -> "Critical"
    }

    fun getStatusColor(score: Int): Int = when (getStatus(score)) {
        HealthStatus.GOOD     -> R.color.colorHealthGood
        HealthStatus.AT_RISK  -> R.color.colorHealthWarn
        HealthStatus.CRITICAL -> R.color.colorHealthDanger
    }

    fun getWarrantyStatus(warrantyEnd: String): WarrantyStatus {
        return try {
            val year = warrantyEnd.split("-")[0].toInt()
            val currentYear = java.util.Calendar.getInstance()
                .get(java.util.Calendar.YEAR)
            when {
                year > currentYear  -> WarrantyStatus.ACTIVE
                year == currentYear -> WarrantyStatus.EXPIRING
                else                -> WarrantyStatus.EXPIRED
            }
        } catch (e: Exception) {
            WarrantyStatus.UNKNOWN
        }
    }
}

enum class HealthStatus { GOOD, AT_RISK, CRITICAL }
enum class WarrantyStatus { ACTIVE, EXPIRING, EXPIRED, UNKNOWN }
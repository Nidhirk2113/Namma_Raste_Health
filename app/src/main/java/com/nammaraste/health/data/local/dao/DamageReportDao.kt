package com.nammaraste.health.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nammaraste.health.data.local.entity.DamageReport

@Dao
interface DamageReportDao {

    @Query("""
        SELECT * FROM damage_reports
        WHERE roadId = :roadId
        ORDER BY timestamp DESC
    """)
    fun getReportsForRoad(roadId: Int): LiveData<List<DamageReport>>

    @Query("SELECT COUNT(*) FROM damage_reports WHERE roadId = :roadId")
    suspend fun getReportCountForRoad(roadId: Int): Int

    @Query("SELECT * FROM damage_reports ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentReports(limit: Int = 10): LiveData<List<DamageReport>>

    @Query("SELECT COUNT(*) FROM damage_reports")
    fun getTotalReportCount(): LiveData<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: DamageReport)

    @Delete
    suspend fun deleteReport(report: DamageReport)

    // Count only UNRESOLVED reports — these hurt the score
    @Query("SELECT COUNT(*) FROM damage_reports WHERE roadId = :roadId AND isResolved = 0")
    suspend fun getOpenReportCountForRoad(roadId: Int): Int

    // Get only open reports for a road (for live health calculation)
    @Query("""
    SELECT * FROM damage_reports 
    WHERE roadId = :roadId AND isResolved = 0
    ORDER BY timestamp DESC
""")
    fun getOpenReportsForRoad(roadId: Int): LiveData<List<DamageReport>>

    // Mark a report as resolved (called when officer confirms repair)
    @Query("UPDATE damage_reports SET isResolved = 1 WHERE id = :reportId")
    suspend fun markAsResolved(reportId: Int)

    // Count of ALL open (unresolved) reports across all roads
    @Query("SELECT COUNT(*) FROM damage_reports WHERE isResolved = 0")
    fun getTotalOpenReportCount(): LiveData<Int>
}
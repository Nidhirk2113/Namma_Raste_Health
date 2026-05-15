package com.nammaraste.health.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.nammaraste.health.data.local.NammaRasteDatabase
import com.nammaraste.health.data.local.entity.DamageReport
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.data.local.entity.Route
import com.nammaraste.health.util.HealthCalculator

class RoadRepository(private val db: NammaRasteDatabase) {

    // ── Roads ──
    fun getAllRoads(): LiveData<List<Road>> = db.roadDao().getAllRoads()
    fun searchRoads(query: String): LiveData<List<Road>> = db.roadDao().searchRoads(query)
    fun getRoadById(id: Int): LiveData<Road> = db.roadDao().getRoadById(id)
    fun getTotalRoadCount(): LiveData<Int> = db.roadDao().getTotalRoadCount()
    suspend fun insertRoad(road: Road) = db.roadDao().insertRoad(road)
    suspend fun updateRoad(road: Road) = db.roadDao().updateRoad(road)
    suspend fun softDeleteRoad(roadId: Int) = db.roadDao().softDeleteRoad(roadId)

    // ── Routes (New) ──
    fun getAllRoutes(): LiveData<List<Route>> = db.routeDao().getAllRoutes()
    fun getRouteById(id: Int): LiveData<Route> = db.routeDao().getRouteById(id)
    suspend fun insertRoute(route: Route) = db.routeDao().insertRoute(route)
    suspend fun updateRoute(route: Route) = db.routeDao().updateRoute(route)
    suspend fun softDeleteRoute(routeId: Int) = db.routeDao().softDeleteRoute(routeId)

    // ── Reports ──
    fun getReportsForRoad(roadId: Int): LiveData<List<DamageReport>> = db.damageReportDao().getReportsForRoad(roadId)
    fun getOpenReportsForRoad(roadId: Int): LiveData<List<DamageReport>> = db.damageReportDao().getOpenReportsForRoad(roadId)
    fun getRecentReports(limit: Int = 10): LiveData<List<DamageReport>> = db.damageReportDao().getRecentReports(limit)
    fun getTotalReportCount(): LiveData<Int> = db.damageReportDao().getTotalReportCount()
    fun getOpenReportCount(): LiveData<Int> = db.damageReportDao().getTotalOpenReportCount()
    suspend fun insertReport(report: DamageReport) = db.damageReportDao().insertReport(report)
    suspend fun getReportCountForRoad(roadId: Int): Int = db.damageReportDao().getReportCountForRoad(roadId)
    suspend fun markReportAsResolved(reportId: Int) = db.damageReportDao().markAsResolved(reportId)

    // ── Live health score ──
    fun getLiveHealthScore(road: Road): LiveData<Int> {
        return db.damageReportDao()
            .getOpenReportsForRoad(road.id)
            .map { openReports ->
                HealthCalculator.calculateScore(road.baseHealthScore, openReports.size)
            }
    }
}
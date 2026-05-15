package com.nammaraste.health.ui.dashboard

import androidx.lifecycle.*
import com.nammaraste.health.data.local.entity.DamageReport
import com.nammaraste.health.data.repository.RoadRepository

class DashboardViewModel(
    private val repository: RoadRepository
) : ViewModel() {

    // Total roads in DB
    val totalRoads: LiveData<Int> = repository.getTotalRoadCount()

    // Total reports ever filed
    val totalReports: LiveData<Int> = repository.getTotalReportCount()

    // Open (unresolved) reports count
    val openReportsCount: LiveData<Int> = repository.getOpenReportCount()

    // Repaired count = total - open
    val repairedCount: LiveData<Int> = MediatorLiveData<Int>().apply {
        fun update() {
            val total = totalReports.value ?: 0
            val open  = openReportsCount.value ?: 0
            value = total - open
        }
        addSource(totalReports)      { update() }
        addSource(openReportsCount)  { update() }
    }

    // 5 most recent reports for dashboard feed
    val recentReports: LiveData<List<DamageReport>> =
        repository.getRecentReports(5)

    // Road name lookup — used to show road name next to each report
    val allRoads = repository.getAllRoads()

    fun getRoadName(roadId: Int): String {
        return allRoads.value?.find { it.id == roadId }?.name ?: "Unknown Road"
    }
}
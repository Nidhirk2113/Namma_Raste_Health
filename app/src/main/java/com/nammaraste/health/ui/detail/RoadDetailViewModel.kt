package com.nammaraste.health.ui.detail

import androidx.lifecycle.*
import com.nammaraste.health.data.local.entity.DamageReport
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.util.HealthCalculator
import kotlinx.coroutines.launch

class RoadDetailViewModel(
    private val repository: RoadRepository
) : ViewModel() {

    private val _roadId = MutableLiveData<Int>()

    // Road data — reacts to roadId being set
    val road: LiveData<Road> = _roadId.switchMap { id ->
        repository.getRoadById(id)
    }

    // ALL reports for this road (open + resolved) for display
    val allReports: LiveData<List<DamageReport>> = _roadId.switchMap { id ->
        repository.getReportsForRoad(id)
    }

    // Open reports count — shown as "X open"
    val openReportCount: LiveData<Int> = _roadId.switchMap { id ->
        repository.getOpenReportsForRoad(id).map { it.size }
    }

    // Live health score — recalculates when open reports change
    val healthScore: LiveData<Int> = MediatorLiveData<Int>().apply {
        fun recalculate() {
            val r = road.value ?: return
            val openCount = openReportCount.value ?: 0
            value = HealthCalculator.calculateScore(r.baseHealthScore, openCount)
        }
        addSource(road)            { recalculate() }
        addSource(openReportCount) { recalculate() }
    }

    fun loadRoad(roadId: Int) {
        _roadId.value = roadId
    }

    fun markAsRepaired(reportId: Int) {
        viewModelScope.launch {
            repository.markReportAsResolved(reportId)
            // LiveData auto-updates — no manual refresh needed
        }
    }

    fun deleteRoad() {
        viewModelScope.launch {
            _roadId.value?.let { id ->
                repository.softDeleteRoad(id)
            }
        }
    }
}
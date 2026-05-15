package com.nammaraste.health.ui.roads

import androidx.lifecycle.*
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.util.HealthCalculator
import kotlinx.coroutines.launch

class RoadsViewModel(private val repository: RoadRepository) : ViewModel() {

    // Holds the current search text
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery

    // All roads from DB — switches between search and full list
    val roads: LiveData<List<Road>> = _searchQuery.switchMap { query ->
        if (query.isBlank()) repository.getAllRoads()
        else repository.searchRoads(query)
    }

    // Active filter chip: "All", "Good", "At Risk", "Critical"
    private val _activeFilter = MutableLiveData("All")

    // Filtered roads — combines DB result + chip filter
    val filteredRoads: LiveData<List<Road>> = MediatorLiveData<List<Road>>().apply {
        fun update() {
            val roadList = roads.value ?: return
            val filter   = _activeFilter.value ?: "All"
            value = if (filter == "All") {
                roadList
            } else {
                roadList.filter { road ->
                    // We need report count to calculate score
                    // For now filter by baseHealthScore
                    // (will be dynamic once we have report counts)
                    val label = HealthCalculator.getStatusLabel(road.baseHealthScore)
                    label == filter
                }
            }
        }
        addSource(roads)         { update() }
        addSource(_activeFilter) { update() }
    }

    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setFilter(filter: String)     { _activeFilter.value = filter }

    // Calculate live health score for a road
    fun getLiveHealthScore(road: Road): LiveData<Int> =
        repository.getLiveHealthScore(road)
}
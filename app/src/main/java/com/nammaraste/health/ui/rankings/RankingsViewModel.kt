package com.nammaraste.health.ui.rankings

import androidx.lifecycle.*
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.util.HealthCalculator

data class RankedRoad(
    val road: Road,
    val score: Int,
    val rank: Int
)

class RankingsViewModel(
    private val repository: RoadRepository
) : ViewModel() {

    // All roads from DB
    private val allRoads: LiveData<List<Road>> = repository.getAllRoads()

    // Holds live scores — roadId → score
    private val _liveScores = MutableLiveData<Map<Int, Int>>(emptyMap())

    // Final ranked list — sorted by score descending
    val rankedRoads: LiveData<List<RankedRoad>> =
        MediatorLiveData<List<RankedRoad>>().apply {
            fun rebuild() {
                val roads  = allRoads.value  ?: return
                val scores = _liveScores.value ?: emptyMap()
                val sorted = roads
                    .map  { road ->
                        val score = scores[road.id] ?: road.baseHealthScore
                        RankedRoad(road, score, 0)
                    }
                    .sortedByDescending { it.score }
                    .mapIndexed { index, ranked ->
                        ranked.copy(rank = index + 1)
                    }
                value = sorted
            }
            addSource(allRoads)    { rebuild() }
            addSource(_liveScores) { rebuild() }
        }

    // Call this from the Fragment to feed live scores in
    fun updateScore(roadId: Int, score: Int) {
        val current = _liveScores.value?.toMutableMap() ?: mutableMapOf()
        current[roadId] = score
        _liveScores.value = current
    }

    fun getLiveHealthScore(road: Road): LiveData<Int> =
        repository.getLiveHealthScore(road)
}
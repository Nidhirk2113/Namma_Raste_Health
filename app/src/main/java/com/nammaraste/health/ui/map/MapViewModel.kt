package com.nammaraste.health.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.nammaraste.health.data.local.entity.DamageReport
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.data.repository.RoadRepository

class MapViewModel(private val repository: RoadRepository) : ViewModel() {

    val allReports: LiveData<List<DamageReport>> =
        repository.getRecentReports(500)

    val allRoads: LiveData<List<Road>> =
        repository.getAllRoads()
}
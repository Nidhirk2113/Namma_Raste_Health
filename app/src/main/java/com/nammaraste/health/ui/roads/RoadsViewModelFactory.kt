package com.nammaraste.health.ui.roads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nammaraste.health.data.repository.RoadRepository

class RoadsViewModelFactory(
    private val repository: RoadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoadsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RoadsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
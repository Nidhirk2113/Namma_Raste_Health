package com.nammaraste.health.ui.rankings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nammaraste.health.data.repository.RoadRepository

class RankingsViewModelFactory(
    private val repository: RoadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RankingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
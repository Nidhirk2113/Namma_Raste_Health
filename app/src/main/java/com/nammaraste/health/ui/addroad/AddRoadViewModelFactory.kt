package com.nammaraste.health.ui.addroad

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nammaraste.health.data.repository.RoadRepository

class AddRoadViewModelFactory(
    private val repository: RoadRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddRoadViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddRoadViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
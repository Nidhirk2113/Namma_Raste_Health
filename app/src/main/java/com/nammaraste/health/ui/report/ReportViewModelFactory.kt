package com.nammaraste.health.ui.report

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nammaraste.health.data.repository.RoadRepository

class ReportViewModelFactory(
    private val repository: RoadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReportViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReportViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
package com.nammaraste.health.ui.addroad

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.data.repository.RoadRepository
import kotlinx.coroutines.launch

class AddRoadViewModel(private val repository: RoadRepository) : ViewModel() {

    sealed class SaveState {
        object Idle    : SaveState()
        object Loading : SaveState()
        object Success : SaveState()
        data class Error(val message: String) : SaveState()
    }

    private val _saveState = MutableLiveData<SaveState>(SaveState.Idle)
    val saveState: LiveData<SaveState> = _saveState

    // Holds the road being edited (null in Add mode)
    private var existingRoad: Road? = null

    // Called by fragment to pre-fill the form in Edit mode
    fun getRoadById(roadId: Int): LiveData<Road> =
        repository.getRoadById(roadId)

    fun setExistingRoad(road: Road) {
        existingRoad = road
    }

    fun isExistingRoadSet(): Boolean = existingRoad != null

    fun saveRoad(
        roadCode: String,
        name: String,
        lengthKm: Double,
        district: String,
        taluka: String,
        hobli: String,
        pincode: String,
        scheme: String,
        constructionYear: Int,
        warrantyEnd: String,
        contractorName: String,
        contractorLicense: String,
        contractorPhone: String
    ) {
        _saveState.value = SaveState.Loading

        viewModelScope.launch {
            try {
                val existing = existingRoad
                if (existing != null) {
                    // EDIT MODE — keep same id, health score, isActive
                    val updated = existing.copy(
                        roadCode          = roadCode,
                        name              = name,
                        lengthKm          = lengthKm,
                        district          = district,
                        taluka            = taluka,
                        hobli             = hobli,
                        pincode           = pincode,
                        scheme            = scheme,
                        constructionYear  = constructionYear,
                        warrantyEnd       = warrantyEnd,
                        contractorName    = contractorName,
                        contractorLicense = contractorLicense,
                        contractorPhone   = contractorPhone
                    )
                    repository.updateRoad(updated)
                } else {
                    // ADD MODE — brand new road
                    val road = Road(
                        roadCode          = roadCode,
                        name              = name,
                        lengthKm          = lengthKm,
                        district          = district,
                        taluka            = taluka,
                        hobli             = hobli,
                        pincode           = pincode,
                        scheme            = scheme,
                        constructionYear  = constructionYear,
                        warrantyEnd       = warrantyEnd,
                        contractorName    = contractorName,
                        contractorLicense = contractorLicense,
                        contractorPhone   = contractorPhone,
                        baseHealthScore   = 100
                    )
                    repository.insertRoad(road)
                }
                _saveState.postValue(SaveState.Success)
            } catch (e: Exception) {
                _saveState.postValue(SaveState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    fun resetState() { _saveState.value = SaveState.Idle }
}
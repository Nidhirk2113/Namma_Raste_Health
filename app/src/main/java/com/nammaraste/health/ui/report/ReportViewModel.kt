package com.nammaraste.health.ui.report

import androidx.lifecycle.*
import com.nammaraste.health.data.local.entity.DamageReport
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.data.repository.RoadRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ReportViewModel(private val repository: RoadRepository) : ViewModel() {

    // All roads — for the dropdown
    val allRoads: LiveData<List<Road>> = repository.getAllRoads()

    // Selected road from dropdown
    private val _selectedRoad = MutableLiveData<Road?>()
    val selectedRoad: LiveData<Road?> = _selectedRoad

    // GPS coordinates
    private val _latitude  = MutableLiveData(0.0)
    private val _longitude = MutableLiveData(0.0)
    val gpsStatus: LiveData<String> = MediatorLiveData<String>().apply {
        fun update() {
            val lat = _latitude.value ?: 0.0
            val lng = _longitude.value ?: 0.0
            value = if (lat != 0.0 && lng != 0.0)
                "%.4f, %.4f".format(lat, lng)
            else
                "Waiting for GPS..."
        }
        addSource(_latitude)  { update() }
        addSource(_longitude) { update() }
    }

    // Captured photo path
    private val _photoPath = MutableLiveData("")
    val photoPath: LiveData<String> = _photoPath

    // Submit state
    sealed class SubmitState {
        object Idle    : SubmitState()
        object Loading : SubmitState()
        object Success : SubmitState()
        data class Error(val message: String) : SubmitState()
    }
    private val _submitState = MutableLiveData<SubmitState>(SubmitState.Idle)
    val submitState: LiveData<SubmitState> = _submitState

    fun selectRoad(road: Road) { _selectedRoad.value = road }

    fun updateLocation(lat: Double, lng: Double) {
        _latitude.value  = lat
        _longitude.value = lng
    }

    fun setPhotoPath(path: String) { _photoPath.value = path }

    fun getCurrentTimestamp(): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date())
    }

    fun submitReport(damageType: String, description: String) {
        val road = _selectedRoad.value
        if (road == null) {
            _submitState.value = SubmitState.Error("Please select a road")
            return
        }
        val photo = _photoPath.value ?: ""
        if (photo.isBlank()) {
            _submitState.value = SubmitState.Error("Please capture a photo")
            return
        }

        _submitState.value = SubmitState.Loading

        val report = DamageReport(
            roadId      = road.id,
            damageType  = damageType,
            description = description,
            photoPath   = photo,
            latitude    = _latitude.value  ?: 0.0,
            longitude   = _longitude.value ?: 0.0,
            timestamp   = System.currentTimeMillis()
        )

        viewModelScope.launch {
            try {
                repository.insertReport(report)
                _submitState.postValue(SubmitState.Success)
            } catch (e: Exception) {
                _submitState.postValue(SubmitState.Error(e.message ?: "Failed to save"))
            }
        }
    }

    fun resetState() { _submitState.value = SubmitState.Idle }
}
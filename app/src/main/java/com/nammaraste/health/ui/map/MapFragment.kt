package com.nammaraste.health.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nammaraste.health.MainActivity
import com.nammaraste.health.R
import com.nammaraste.health.databinding.FragmentMapBinding

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: MapViewModel
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity() as MainActivity).repository
        val factory = MapViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[MapViewModel::class.java]

        val mapFragment = childFragmentManager
            .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        map.uiSettings.isZoomControlsEnabled = true
        map.uiSettings.isCompassEnabled = true

        // Default center — Karnataka
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(LatLng(15.3173, 75.7139), 7f)
        )

        // Plot pins when both reports and roads are loaded
        viewModel.allReports.observe(viewLifecycleOwner) { plotPins() }
        viewModel.allRoads.observe(viewLifecycleOwner)   { plotPins() }
    }

    private fun plotPins() {
        val map     = googleMap ?: return
        val reports = viewModel.allReports.value ?: return
        val roads   = viewModel.allRoads.value   ?: emptyList()

        map.clear()

        var firstValidLatLng: LatLng? = null

        reports.forEach { report ->
            if (report.latitude == 0.0 && report.longitude == 0.0) return@forEach

            val position = LatLng(report.latitude, report.longitude)
            if (firstValidLatLng == null) firstValidLatLng = position

            val roadName = roads.find { it.id == report.roadId }?.name ?: "Unknown Road"
            val color = if (report.isResolved)
                BitmapDescriptorFactory.HUE_GREEN
            else
                BitmapDescriptorFactory.HUE_RED

            map.addMarker(
                MarkerOptions()
                    .position(position)
                    .title(report.damageType)
                    .snippet("$roadName · ${if (report.isResolved) "Repaired ✅" else "Open 🔴"}")
                    .icon(BitmapDescriptorFactory.defaultMarker(color))
            )
        }

        // Zoom into first valid pin if available
        firstValidLatLng?.let {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(it, 11f))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
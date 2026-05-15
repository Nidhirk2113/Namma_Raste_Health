package com.nammaraste.health.ui.report

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.nammaraste.health.MainActivity
import com.nammaraste.health.R
import com.nammaraste.health.databinding.FragmentReportBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ReportFragment : Fragment() {

    private var _binding: FragmentReportBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ReportViewModel

    // Holds the URI of the photo we're about to capture
    private var photoUri: Uri? = null

    // Camera launcher
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            viewModel.setPhotoPath(photoUri.toString())
            binding.ivPhotoPreview.setImageURI(photoUri)
            binding.ivPhotoPreview.visibility   = View.VISIBLE
            binding.layoutCameraPrompt.visibility = View.GONE
            binding.tvPhotoError.visibility     = View.GONE
        }
    }

    // Permission launcher
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted   = permissions[Manifest.permission.CAMERA] == true
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (cameraGranted) openCamera()
        if (locationGranted) fetchLocation()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity() as MainActivity).repository
        val factory = ReportViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[ReportViewModel::class.java]

        setupRoadDropdown()
        setupCameraButton()
        setupSubmitButton()
        observeViewModel()
        requestLocationPermission()
    }

    // Populate road dropdown from DB
    private fun setupRoadDropdown() {
        viewModel.allRoads.observe(viewLifecycleOwner) { roads ->
            val names = roads.map { it.name }
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                names
            )
            binding.actvRoad.setAdapter(adapter)
            binding.actvRoad.setOnItemClickListener { _, _, position, _ ->
                viewModel.selectRoad(roads[position])
            }
        }
    }

    private fun setupCameraButton() {
        val onCapture = View.OnClickListener {
            if (hasCameraPermission()) openCamera()
            else requestCameraPermission()
        }
        binding.btnCapture.setOnClickListener(onCapture)
        binding.layoutCameraPrompt.setOnClickListener(onCapture)
    }

    private fun openCamera() {
        val photoFile = createImageFile()
        photoUri = FileProvider.getUriForFile(
            requireContext(),
            "com.nammaraste.health.fileprovider",
            photoFile
        )
        cameraLauncher.launch(photoUri)
    }

    private fun createImageFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = requireContext()
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("DAMAGE_${timestamp}_", ".jpg", storageDir)
    }

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        val client = LocationServices.getFusedLocationProviderClient(requireActivity())
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    viewModel.updateLocation(location.latitude, location.longitude)
                }
            }
    }

    private fun setupSubmitButton() {
        binding.btnSubmitReport.setOnClickListener {
            val damageType = getSelectedDamageType()
            if (damageType == null) {
                binding.tvDamageTypeError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            binding.tvDamageTypeError.visibility = View.GONE

            if (viewModel.photoPath.value.isNullOrBlank()) {
                binding.tvPhotoError.visibility = View.VISIBLE
                return@setOnClickListener
            }
            binding.tvPhotoError.visibility = View.GONE

            viewModel.submitReport(
                damageType  = damageType,
                description = binding.etDescription.text.toString().trim()
            )
        }
    }

    private fun getSelectedDamageType(): String? {
        val chipId = binding.chipGroupDamageType.checkedChipId
        if (chipId == View.NO_ID) return null
        return when (chipId) {
            R.id.chipPothole     -> "Pothole"
            R.id.chipWaterLogging -> "Water Logging"
            R.id.chipErosion     -> "Road Erosion"
            R.id.chipCloggedDrain -> "Clogged Drain"
            R.id.chipOverload    -> "Overload Damage"
            R.id.chipCrack       -> "Surface Crack"
            else -> null
        }
    }

    private fun observeViewModel() {
        // GPS status
        viewModel.gpsStatus.observe(viewLifecycleOwner) { status ->
            binding.tvGpsStatus.text = status
        }

        // Timestamp — show current time live
        binding.tvTimestamp.text = viewModel.getCurrentTimestamp()

        // Submit state
        viewModel.submitState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ReportViewModel.SubmitState.Loading -> {
                    binding.btnSubmitReport.isEnabled = false
                    binding.btnSubmitReport.text = "Submitting..."
                }
                is ReportViewModel.SubmitState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "✅ Report submitted! Road health updated.",
                        Toast.LENGTH_SHORT
                    ).show()
                    viewModel.resetState()
                    clearForm()
                }
                is ReportViewModel.SubmitState.Error -> {
                    binding.btnSubmitReport.isEnabled = true
                    binding.btnSubmitReport.text = "Submit Damage Report"
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                is ReportViewModel.SubmitState.Idle -> {
                    binding.btnSubmitReport.isEnabled = true
                    binding.btnSubmitReport.text = "Submit Damage Report"
                }
            }
        }
    }

    private fun clearForm() {
        binding.actvRoad.setText("")
        binding.chipGroupDamageType.clearCheck()
        binding.ivPhotoPreview.visibility    = View.GONE
        binding.layoutCameraPrompt.visibility = View.VISIBLE
        binding.etDescription.setText("")
        binding.tvTimestamp.text = viewModel.getCurrentTimestamp()
        photoUri = null
    }

    // Permission helpers
    private fun hasCameraPermission() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
        requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
    }

    private fun requestLocationPermission() {
        if (hasLocationPermission()) fetchLocation()
        else permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.nammaraste.health.ui.addroad

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nammaraste.health.MainActivity
import com.nammaraste.health.databinding.FragmentAddRoadBinding
import android.widget.ArrayAdapter

class AddRoadFragment : Fragment() {

    private var _binding: FragmentAddRoadBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AddRoadViewModel
    private val args: AddRoadFragmentArgs by navArgs()

    // If roadId is -1 we are in ADD mode Otherwise EDIT mode.
    private val isEditMode get() = args.roadId != -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddRoadBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun setupSchemeDropdown() {
        val schemes = listOf(
            "PMGSY-I",
            "PMGSY-II",
            "PMGSY-III",
            "State Built Road",
            "Municipal Authority",
            "Other"
        )

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            schemes
        )

        binding.actvScheme.setAdapter(adapter)

        // Prevent typing (only allow selection)
        binding.actvScheme.inputType = 0
        binding.actvScheme.isFocusable = false
        binding.actvScheme.isClickable = true

        // Show dropdown on click
        binding.actvScheme.setOnClickListener {
            binding.actvScheme.showDropDown()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity() as MainActivity).repository
        val factory = AddRoadViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AddRoadViewModel::class.java]

        setupSchemeDropdown()

        if (isEditMode) {
            binding.btnSaveRoad.text = "Update Road"
            // Load existing road and pre-fill all fields
            viewModel.getRoadById(args.roadId).observe(viewLifecycleOwner) { road ->
                if (road != null && viewModel.isExistingRoadSet().not()) {
                    viewModel.setExistingRoad(road)
                    binding.etRoadCode.setText(road.roadCode)
                    binding.etRoadName.setText(road.name)
                    binding.etLength.setText(road.lengthKm.toString())
                    binding.etConstructionYear.setText(road.constructionYear.toString())
                    binding.etDistrict.setText(road.district)
                    binding.etTaluka.setText(road.taluka)
                    binding.etHobli.setText(road.hobli)
                    binding.etPincode.setText(road.pincode)
                    binding.actvScheme.setText(road.scheme, false)
                    binding.etWarrantyEnd.setText(road.warrantyEnd)
                    binding.etContractorName.setText(road.contractorName)
                    binding.etLicense.setText(road.contractorLicense)
                    binding.etPhone.setText(road.contractorPhone)
                }
            }
        }

        setupSaveButton()
        observeSaveState()


    }

    private fun setupSaveButton() {
        binding.btnSaveRoad.setOnClickListener {
            if (validateInputs()) {
                viewModel.saveRoad(
                    roadCode          = binding.etRoadCode.text.toString().trim(),
                    name              = binding.etRoadName.text.toString().trim(),
                    lengthKm          = binding.etLength.text.toString().toDouble(),
                    district          = binding.etDistrict.text.toString().trim(),
                    taluka            = binding.etTaluka.text.toString().trim(),
                    hobli             = binding.etHobli.text.toString().trim(),
                    pincode           = binding.etPincode.text.toString().trim(),
                    scheme            = binding.actvScheme.text.toString().trim(),
                    constructionYear  = binding.etConstructionYear.text.toString().toInt(),
                    warrantyEnd       = binding.etWarrantyEnd.text.toString().trim(),
                    contractorName    = binding.etContractorName.text.toString().trim(),
                    contractorLicense = binding.etLicense.text.toString().trim(),
                    contractorPhone   = binding.etPhone.text.toString().trim()
                )
            }
        }
    }

    private fun observeSaveState() {
        viewModel.saveState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AddRoadViewModel.SaveState.Loading -> {
                    binding.btnSaveRoad.isEnabled = false
                    binding.btnSaveRoad.text = if (isEditMode) "Updating..." else "Saving..."
                }
                is AddRoadViewModel.SaveState.Success -> {
                    val msg = if (isEditMode) "✅ Road updated!" else "✅ Road saved!"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    viewModel.resetState()
                    findNavController().navigateUp()
                }
                is AddRoadViewModel.SaveState.Error -> {
                    binding.btnSaveRoad.isEnabled = true
                    binding.btnSaveRoad.text = if (isEditMode) "Update Road" else "Save Road to Database"
                    Toast.makeText(requireContext(), "❌ ${state.message}", Toast.LENGTH_LONG).show()
                    viewModel.resetState()
                }
                is AddRoadViewModel.SaveState.Idle -> {
                    binding.btnSaveRoad.isEnabled = true
                    binding.btnSaveRoad.text = if (isEditMode) "Update Road" else "Save Road to Database"
                }
            }
        }
    }

    private fun validateInputs(): Boolean {
        var isValid = true
        fun check(
            text: String?,
            layout: com.google.android.material.textfield.TextInputLayout,
            msg: String = "Required"
        ) {
            if (text.isNullOrBlank()) { layout.error = msg; isValid = false }
            else layout.error = null
        }
        check(binding.etRoadCode.text.toString(),         binding.tilRoadCode,         "Road code required")
        check(binding.etRoadName.text.toString(),         binding.tilRoadName,         "Road name required")
        check(binding.etLength.text.toString(),           binding.tilLength)
        check(binding.etConstructionYear.text.toString(), binding.tilConstructionYear)
        check(binding.etDistrict.text.toString(),         binding.tilDistrict)
        check(binding.etTaluka.text.toString(),           binding.tilTaluka)
        check(binding.etHobli.text.toString(),            binding.tilHobli)
        check(binding.etPincode.text.toString(),          binding.tilPincode)
        check(binding.actvScheme.text.toString(),           binding.tilScheme)
        check(binding.etWarrantyEnd.text.toString(),      binding.tilWarrantyEnd)
        check(binding.etContractorName.text.toString(),   binding.tilContractorName)
        check(binding.etLicense.text.toString(),          binding.tilLicense)
        check(binding.etPhone.text.toString(),            binding.tilPhone)
        return isValid
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.nammaraste.health.ui.roads

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammaraste.health.MainActivity
import com.nammaraste.health.R
import com.nammaraste.health.databinding.FragmentRoadsBinding

class RoadsFragment : Fragment() {

    private var _binding: FragmentRoadsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RoadsViewModel
    private lateinit var adapter: RoadAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoadsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity() as MainActivity).repository
        val factory = RoadsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RoadsViewModel::class.java]

        setupRecyclerView()
        setupSearch()
        setupFilterChips()
        observeRoads()

        // FAB → Add Road screen
        binding.fabAddRoad.setOnClickListener {
            findNavController().navigate(R.id.addRoadFragment)
        }
    }

    private fun setupRecyclerView() {
        adapter = RoadAdapter { road ->
            // Navigate to detail screen, passing road ID
            val action = RoadsFragmentDirections
                .actionRoadsFragmentToRoadDetailFragment(road.id)
            findNavController().navigate(action)
        }
        binding.rvRoads.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRoads.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.setSearchQuery(s.toString())
            }
        })
    }

    private fun setupFilterChips() {
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            val filter = when {
                checkedIds.contains(R.id.chipGood)     -> "Good"
                checkedIds.contains(R.id.chipAtRisk)   -> "At Risk"
                checkedIds.contains(R.id.chipCritical) -> "Critical"
                else                                   -> "All"
            }
            viewModel.setFilter(filter)
        }
    }

    private fun observeRoads() {
        viewModel.filteredRoads.observe(viewLifecycleOwner) { roads ->
            adapter.submitList(roads)

            // Show / hide empty state
            binding.layoutEmptyState.visibility =
                if (roads.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRoads.visibility =
                if (roads.isEmpty()) View.GONE else View.VISIBLE

            // Observe live health score for each road
            roads.forEach { road ->
                viewModel.getLiveHealthScore(road)
                    .observe(viewLifecycleOwner) { score ->
                        adapter.updateHealthScore(road.id, score)
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.nammaraste.health.ui.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nammaraste.health.MainActivity
import com.nammaraste.health.databinding.FragmentRankingsBinding

class RankingsFragment : Fragment() {

    private var _binding: FragmentRankingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RankingsViewModel
    private lateinit var adapter: RankingsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRankingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity() as MainActivity).repository
        val factory = RankingsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RankingsViewModel::class.java]

        setupRecyclerView()
        observeRankings()
    }

    private fun setupRecyclerView() {
        adapter = RankingsAdapter()
        binding.rvRankings.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRankings.adapter = adapter
    }

    private fun observeRankings() {
        // First observe all roads to wire up live scores
        val repository = (requireActivity() as MainActivity).repository
        repository.getAllRoads().observe(viewLifecycleOwner) { roads ->
            roads.forEach { road ->
                viewModel.getLiveHealthScore(road).observe(viewLifecycleOwner) { score ->
                    viewModel.updateScore(road.id, score)
                }
            }
        }

        // Then observe ranked list for display
        viewModel.rankedRoads.observe(viewLifecycleOwner) { ranked ->
            adapter.submitList(ranked)
            binding.layoutRankingsEmpty.visibility =
                if (ranked.isEmpty()) View.VISIBLE else View.GONE
            binding.rvRankings.visibility =
                if (ranked.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
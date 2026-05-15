package com.nammaraste.health.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nammaraste.health.MainActivity
import com.nammaraste.health.databinding.FragmentDashboardBinding
import com.nammaraste.health.databinding.ItemRecentReportBinding
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity() as MainActivity).repository
        val factory = DashboardViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[DashboardViewModel::class.java]

        // Show today's date in the banner
        binding.tvBannerDate.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        observeMetrics()
        observeRoadStats()
        observeRecentReports()
    }

    private fun observeMetrics() {
        viewModel.totalRoads.observe(viewLifecycleOwner) { binding.tvTotalRoads.text = it.toString() }
        viewModel.totalReports.observe(viewLifecycleOwner) { binding.tvTotalReports.text = it.toString() }
        viewModel.openReportsCount.observe(viewLifecycleOwner) { binding.tvOpenReports.text = it.toString() }
        viewModel.repairedCount.observe(viewLifecycleOwner) { binding.tvRepairedCount.text = it.toString() }
    }

    private fun observeRoadStats() {
        viewModel.allRoads.observe(viewLifecycleOwner) { roads ->
            if (roads.isEmpty()) return@observe

            val highPriority = roads.count { it.maintenancePriority.equals("High", ignoreCase = true) }
            binding.tvHighPriorityCount.text = "$highPriority roads"

            val avgPotholes = roads.map { it.potholeCount }.average()
            binding.tvAvgPotholes.text = "%.1f per road".format(avgPotholes)

            val avgTraffic = roads.map { it.avgTrafficPerDay }.average().toLong()
            binding.tvAvgTraffic.text = NumberFormat.getNumberInstance(Locale("en", "IN")).format(avgTraffic) + " /day"
        }
    }

    private fun observeRecentReports() {
        viewModel.recentReports.observe(viewLifecycleOwner) { reports ->
            binding.layoutRecentContainer.removeAllViews()

            if (reports.isEmpty()) {
                binding.layoutNoRecent.visibility = View.VISIBLE
                return@observe
            }
            binding.layoutNoRecent.visibility = View.GONE

            reports.forEach { report ->
                val itemBinding = ItemRecentReportBinding.inflate(layoutInflater, binding.layoutRecentContainer, false)
                itemBinding.tvRecentDamageType.text = report.damageType

                viewModel.allRoads.observe(viewLifecycleOwner) { roads ->
                    val roadName = roads.find { it.id == report.roadId }?.name ?: "Unknown Road"
                    itemBinding.tvRecentRoadName.text = roadName
                }

                itemBinding.tvRecentTime.text = getTimeAgo(report.timestamp)

                val dotColor = if (report.isResolved) com.nammaraste.health.R.color.colorHealthGood
                               else com.nammaraste.health.R.color.colorHealthDanger
                itemBinding.viewStatusDot.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(
                        androidx.core.content.ContextCompat.getColor(requireContext(), dotColor)
                    )

                binding.layoutRecentContainer.addView(itemBinding.root)
            }
        }
    }

    private fun getTimeAgo(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < TimeUnit.MINUTES.toMillis(1)  -> "Just now"
            diff < TimeUnit.HOURS.toMillis(1)    -> "${TimeUnit.MILLISECONDS.toMinutes(diff)}m ago"
            diff < TimeUnit.DAYS.toMillis(1)     -> "${TimeUnit.MILLISECONDS.toHours(diff)}h ago"
            diff < TimeUnit.DAYS.toMillis(30)    -> "${TimeUnit.MILLISECONDS.toDays(diff)}d ago"
            else -> SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

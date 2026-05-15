package com.nammaraste.health.ui.detail

import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.nammaraste.health.MainActivity
import com.nammaraste.health.R
import com.nammaraste.health.data.local.entity.DamageReport
import com.nammaraste.health.databinding.FragmentRoadDetailBinding
import com.nammaraste.health.databinding.ItemDamageReportBinding
import com.nammaraste.health.util.HealthCalculator
import com.nammaraste.health.util.HealthStatus
import com.nammaraste.health.util.PdfReportGenerator
import com.nammaraste.health.util.WarrantyStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class RoadDetailFragment : Fragment() {

    private var _binding: FragmentRoadDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RoadDetailViewModel
    private val args: RoadDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRoadDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val repository = (requireActivity() as MainActivity).repository
        val factory = RoadDetailViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RoadDetailViewModel::class.java]
        viewModel.loadRoad(args.roadId)

        observeRoad()
        observeHealthScore()
        observeReports()
    }

    private fun observeRoad() {
        viewModel.road.observe(viewLifecycleOwner) { road ->
            if (road == null) return@observe

            binding.tvDetailRoadName.text = road.name
            binding.tvDetailRoadCode.text = "${road.roadCode} · ${road.taluka}"

            // CSV-mapped fields
            binding.tvDetailDistrict.text = road.district        // area
            binding.tvDetailTaluka.text   = road.taluka          // road_type
            binding.tvDetailHobli.text    = road.hobli           // surface_type
            binding.tvDetailLength.text   = "${road.lengthKm} km"
            binding.tvDetailYear.text     = "${road.constructionYear}"
            binding.tvDetailScheme.text   = road.lastInspectionDate.ifBlank { "N/A" }

            // New metric fields
            binding.tvDetailPotholes.text    = "${road.potholeCount} potholes"
            binding.tvDetailTraffic.text     = NumberFormat.getNumberInstance(Locale("en", "IN"))
                                                    .format(road.avgTrafficPerDay) + " vehicles/day"
            binding.tvDetailRisk.text        = "%.1f / 10".format(road.accidentRiskScore)
            binding.tvDetailStreetlight.text = road.streetlightAvailability.ifBlank { "N/A" }
            binding.tvDetailDrainage.text    = road.drainageCondition.ifBlank { "N/A" }
            binding.tvDetailRepairCost.text  = "₹" + NumberFormat.getNumberInstance(Locale("en", "IN"))
                                                    .format(road.estimatedRepairCostInr)

            binding.tvDetailContractorName.text = road.contractorName
            binding.tvDetailLicense.text        = road.contractorLicense

            // Condition status badge
            val conditionStatus = road.conditionStatus.ifBlank { "Good" }
            val (condBg, condColor) = when (conditionStatus.lowercase()) {
                "good"               -> Pair(R.drawable.bg_badge_green, R.color.colorHealthGood)
                "moderate"           -> Pair(R.drawable.bg_badge_green, R.color.colorHealthGood)
                "under maintenance"  -> Pair(R.drawable.bg_badge_amber, R.color.colorHealthWarn)
                "damaged"            -> Pair(R.drawable.bg_badge_amber, R.color.colorHealthWarn)
                else                 -> Pair(R.drawable.bg_badge_red,   R.color.colorHealthDanger)
            }
            binding.tvDetailCondition.text = conditionStatus
            binding.tvDetailCondition.setBackgroundResource(condBg)
            binding.tvDetailCondition.setTextColor(ContextCompat.getColor(requireContext(), condColor))

            // Warranty status
            val warranty = HealthCalculator.getWarrantyStatus(road.warrantyEnd)
            when (warranty) {
                WarrantyStatus.ACTIVE -> {
                    binding.tvDetailWarranty.text = "✅ Under Warranty"
                    binding.tvDetailWarranty.setBackgroundResource(R.drawable.bg_badge_green)
                    binding.tvDetailWarranty.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorHealthGood))
                }
                WarrantyStatus.EXPIRING -> {
                    binding.tvDetailWarranty.text = "⚠️ Warranty Expiring"
                    binding.tvDetailWarranty.setBackgroundResource(R.drawable.bg_badge_amber)
                    binding.tvDetailWarranty.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorHealthWarn))
                }
                WarrantyStatus.EXPIRED -> {
                    binding.tvDetailWarranty.text = "❌ Warranty Expired"
                    binding.tvDetailWarranty.setBackgroundResource(R.drawable.bg_badge_red)
                    binding.tvDetailWarranty.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorHealthDanger))
                }
                else -> binding.tvDetailWarranty.text = road.warrantyEnd
            }

            binding.btnCallContractor.setOnClickListener {
                startActivity(Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:${road.contractorPhone}") })
            }

            binding.btnEditRoad.setOnClickListener {
                findNavController().navigate(
                    RoadDetailFragmentDirections.actionRoadDetailFragmentToAddRoadFragment(road.id)
                )
            }

            binding.btnDeleteRoad.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Road")
                    .setMessage("Delete \"${road.name}\"?\n\nThe road will be hidden. Damage reports are preserved.")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.deleteRoad()
                        Toast.makeText(requireContext(), "Road deleted", Toast.LENGTH_SHORT).show()
                        findNavController().navigateUp()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            binding.btnExportPdf.setOnClickListener {
                val reports = viewModel.allReports.value ?: emptyList()
                val score   = viewModel.healthScore.value ?: road.baseHealthScore
                exportToPdf(road, reports, score)
            }
        }
    }

    private fun observeHealthScore() {
        viewModel.healthScore.observe(viewLifecycleOwner) { score ->
            val colorRes = HealthCalculator.getStatusColor(score)
            val color    = ContextCompat.getColor(requireContext(), colorRes)
            binding.tvDetailScore.text = score.toString()
            binding.tvDetailScore.setTextColor(color)
            binding.tvDetailHealthLabel.text = HealthCalculator.getStatusLabel(score)
            binding.tvDetailHealthLabel.setTextColor(color)
            binding.pbDetailHealth.progress = score
            binding.pbDetailHealth.progressTintList = ColorStateList.valueOf(color)
        }
        viewModel.openReportCount.observe(viewLifecycleOwner) { count ->
            binding.tvReportCount.text = "$count open"
        }
    }

    private fun observeReports() {
        viewModel.allReports.observe(viewLifecycleOwner) { reports ->
            binding.layoutReportsContainer.removeAllViews()
            if (reports.isEmpty()) {
                binding.layoutNoReports.visibility = View.VISIBLE
                return@observe
            }
            binding.layoutNoReports.visibility = View.GONE
            reports.forEach { addReportCard(it) }
        }
    }

    private fun addReportCard(report: DamageReport) {
        val itemBinding = ItemDamageReportBinding.inflate(layoutInflater, binding.layoutReportsContainer, false)
        itemBinding.tvDamageType.text = report.damageType
        itemBinding.tvDamageDescription.text = report.description.ifBlank { "No description provided" }
        itemBinding.tvDamageLocation.text =
            if (report.latitude != 0.0) "📍 %.4f, %.4f".format(report.latitude, report.longitude)
            else "📍 Location not captured"

        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        itemBinding.tvDamageTime.text = "🕐 ${sdf.format(Date(report.timestamp))}"

        if (report.photoPath.isNotBlank()) {
            try {
                itemBinding.ivDamagePhoto.setImageURI(Uri.parse(report.photoPath))
                itemBinding.ivDamagePhoto.visibility = View.VISIBLE
            } catch (e: Exception) {
                itemBinding.ivDamagePhoto.visibility = View.GONE
            }
        }

        if (report.isResolved) {
            itemBinding.tvResolvedBadge.text = "✅ Repaired"
            itemBinding.tvResolvedBadge.setBackgroundResource(R.drawable.bg_badge_green)
            itemBinding.tvResolvedBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorHealthGood))
            itemBinding.btnMarkRepaired.visibility = View.GONE
        } else {
            itemBinding.tvResolvedBadge.text = "🔴 Open"
            itemBinding.tvResolvedBadge.setBackgroundResource(R.drawable.bg_badge_red)
            itemBinding.tvResolvedBadge.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorHealthDanger))
            itemBinding.btnMarkRepaired.visibility = View.VISIBLE
            itemBinding.btnMarkRepaired.setOnClickListener { viewModel.markAsRepaired(report.id) }
        }
        binding.layoutReportsContainer.addView(itemBinding.root)
    }

    private fun exportToPdf(road: com.nammaraste.health.data.local.entity.Road, reports: List<DamageReport>, score: Int) {
        binding.btnExportPdf.isEnabled = false
        binding.btnExportPdf.text = "Generating PDF..."
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val file = PdfReportGenerator.generateRoadReport(requireContext(), road, score, reports)
                withContext(Dispatchers.Main) {
                    binding.btnExportPdf.isEnabled = true
                    binding.btnExportPdf.text = "📄  Export as PDF"
                    val uri = FileProvider.getUriForFile(requireContext(), "com.nammaraste.health.fileprovider", file)
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "application/pdf"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_SUBJECT, "Road Report — ${road.name}")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    startActivity(Intent.createChooser(intent, "Share Report PDF"))
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.btnExportPdf.isEnabled = true
                    binding.btnExportPdf.text = "📄  Export as PDF"
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

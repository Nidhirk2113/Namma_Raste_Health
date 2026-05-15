package com.nammaraste.health.ui.roads

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaraste.health.R
import com.nammaraste.health.data.local.entity.Road
import com.nammaraste.health.databinding.ItemRoadBinding
import com.nammaraste.health.util.HealthCalculator
import com.nammaraste.health.util.HealthStatus

class RoadAdapter(
    private val onRoadClick: (Road) -> Unit
) : ListAdapter<Road, RoadAdapter.RoadViewHolder>(DiffCallback) {

    private val healthScores = mutableMapOf<Int, Int>()

    fun updateHealthScore(roadId: Int, score: Int) {
        healthScores[roadId] = score
        currentList.indexOfFirst { it.id == roadId }
            .takeIf { it >= 0 }?.let { notifyItemChanged(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoadViewHolder {
        val binding = ItemRoadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RoadViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoadViewHolder, position: Int) {
        val road  = getItem(position)
        val score = healthScores[road.id] ?: road.baseHealthScore
        holder.bind(road, score, onRoadClick)
    }

    class RoadViewHolder(private val binding: ItemRoadBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(road: Road, score: Int, onClick: (Road) -> Unit) {
            val ctx = binding.root.context

            binding.tvRoadName.text = road.name
            // Use CSV fields: district=area, taluka=road_type
            binding.tvRoadMeta.text = "${road.district} · ${road.taluka} · ${road.lengthKm} km"
            binding.pbHealthScore.progress = score
            binding.tvHealthScore.text = "$score%"

            // Pothole count
            binding.tvPotholes.text = "🕳️ ${road.potholeCount} potholes"

            val status = HealthCalculator.getStatus(score)

            val (barColor, badgeBg, badgeTextColor, label) = when (status) {
                HealthStatus.GOOD     -> Tuple4(R.color.colorHealthGood,   R.drawable.bg_badge_green, R.color.colorHealthGood,   "Good")
                HealthStatus.AT_RISK  -> Tuple4(R.color.colorHealthWarn,   R.drawable.bg_badge_amber, R.color.colorHealthWarn,   "At Risk")
                HealthStatus.CRITICAL -> Tuple4(R.color.colorHealthDanger, R.drawable.bg_badge_red,   R.color.colorHealthDanger, "Critical")
            }

            val color = ContextCompat.getColor(ctx, barColor)
            binding.viewHealthBar.setBackgroundColor(color)
            binding.tvHealthBadge.text = label
            binding.tvHealthBadge.setBackgroundResource(badgeBg)
            binding.tvHealthBadge.setTextColor(ContextCompat.getColor(ctx, badgeTextColor))
            binding.tvHealthScore.setTextColor(color)
            binding.pbHealthScore.progressTintList = ColorStateList.valueOf(color)

            // Maintenance priority badge
            val (priorityBg, priorityColor, priorityLabel) = when (road.maintenancePriority.lowercase()) {
                "high"   -> Triple(R.drawable.bg_badge_red,   R.color.colorHealthDanger, "High Priority")
                "medium" -> Triple(R.drawable.bg_badge_amber, R.color.colorHealthWarn,   "Medium Priority")
                else     -> Triple(R.drawable.bg_badge_green, R.color.colorHealthGood,   "Low Priority")
            }
            binding.tvPriority.text = priorityLabel
            binding.tvPriority.setBackgroundResource(priorityBg)
            binding.tvPriority.setTextColor(ContextCompat.getColor(ctx, priorityColor))

            binding.root.setOnClickListener { onClick(road) }
        }
    }

    data class Tuple4(val bar: Int, val bg: Int, val text: Int, val label: String)

    companion object DiffCallback : DiffUtil.ItemCallback<Road>() {
        override fun areItemsTheSame(oldItem: Road, newItem: Road) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Road, newItem: Road) = oldItem == newItem
    }
}

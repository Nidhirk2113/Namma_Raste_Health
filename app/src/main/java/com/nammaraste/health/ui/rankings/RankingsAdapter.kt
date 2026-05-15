package com.nammaraste.health.ui.rankings

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nammaraste.health.R
import com.nammaraste.health.databinding.ItemRankingBinding
import com.nammaraste.health.util.HealthCalculator
import com.nammaraste.health.util.HealthStatus

class RankingsAdapter : ListAdapter<RankedRoad, RankingsAdapter.RankViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankViewHolder {
        val binding = ItemRankingBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RankViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RankViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class RankViewHolder(
        private val binding: ItemRankingBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ranked: RankedRoad) {
            val ctx   = binding.root.context
            val road  = ranked.road
            val score = ranked.score

            // Rank medal
            binding.tvRank.text = when (ranked.rank) {
                1    -> "🥇"
                2    -> "🥈"
                3    -> "🥉"
                else -> "#${ranked.rank}"
            }

            binding.tvRankRoadName.text = road.name
            binding.tvRankMeta.text     = "${road.district} · ${road.taluka}"
            binding.tvRankScore.text    = "$score%"
            binding.pbRankHealth.progress = score

            val status = HealthCalculator.getStatus(score)
            val (colorRes, badgeBg, label) = when (status) {
                HealthStatus.GOOD     -> Triple(
                    R.color.colorHealthGood,
                    R.drawable.bg_badge_green,
                    "Good"
                )
                HealthStatus.AT_RISK  -> Triple(
                    R.color.colorHealthWarn,
                    R.drawable.bg_badge_amber,
                    "At Risk"
                )
                HealthStatus.CRITICAL -> Triple(
                    R.color.colorHealthDanger,
                    R.drawable.bg_badge_red,
                    "Critical"
                )
            }

            val color = ContextCompat.getColor(ctx, colorRes)
            binding.tvRankScore.setTextColor(color)
            binding.pbRankHealth.progressTintList = ColorStateList.valueOf(color)
            binding.tvRankBadge.text = label
            binding.tvRankBadge.setBackgroundResource(badgeBg)
            binding.tvRankBadge.setTextColor(color)
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<RankedRoad>() {
        override fun areItemsTheSame(old: RankedRoad, new: RankedRoad) =
            old.road.id == new.road.id
        override fun areContentsTheSame(old: RankedRoad, new: RankedRoad) =
            old == new
    }
}
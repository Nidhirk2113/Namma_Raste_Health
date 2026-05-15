package com.nammaraste.health

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.nammaraste.health.data.local.NammaRasteDatabase
import com.nammaraste.health.data.repository.RoadRepository
import com.nammaraste.health.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    val database by lazy { NammaRasteDatabase.getDatabase(this) }
    val repository by lazy { RoadRepository(database) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Match the brand's dark aesthetic
        binding.root.setBackgroundColor(ContextCompat.getColor(this, R.color.brand_black))
        window.statusBarColor = ContextCompat.getColor(this, R.color.brand_black)
        
        setupNavigation()
        observeRoadCount()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateToolbar(destination.id)
            updateBottomNavVisibility(destination.id)
        }
    }

    private fun observeRoadCount() {
        repository.getTotalRoadCount().observe(this) { count ->
            binding.tvToolbarRoadCount.text = count.toString()
        }
    }

    private fun updateToolbar(destinationId: Int) {
        binding.tvToolbarTitle.text = when (destinationId) {
            R.id.dashboardFragment  -> "City Health Overview"
            R.id.roadsFragment      -> "Infrastructure Directory"
            R.id.reportFragment     -> "Diagnostic Scan"
            R.id.rankingsFragment   -> "Safety Rankings"
            R.id.mapFragment        -> "Live Damage Map"
            R.id.addRoadFragment    -> "Register New Asset"
            R.id.roadDetailFragment -> "Structural Health Detail"
            else -> getString(R.string.app_name)
        }

        binding.tvToolbarTitle.setTextColor(ContextCompat.getColor(this, R.color.text_white))

        when (destinationId) {
            R.id.mapFragment -> {
                binding.toolbar.visibility = View.VISIBLE
                binding.toolbar.elevation = 0f
                binding.layoutRoadCount.visibility = View.GONE
                binding.toolbar.setBackgroundColor(Color.TRANSPARENT)
            }
            R.id.reportFragment -> {
                binding.toolbar.visibility = View.GONE
            }
            else -> {
                binding.toolbar.visibility = View.VISIBLE
                binding.layoutRoadCount.visibility = View.VISIBLE
                binding.toolbar.elevation = 0f
                binding.toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.brand_black))
            }
        }
    }

    private fun updateBottomNavVisibility(destinationId: Int) {
        when (destinationId) {
            R.id.addRoadFragment,
            R.id.roadDetailFragment,
            R.id.reportFragment -> binding.bottomNavigationView.visibility = View.GONE
            else -> binding.bottomNavigationView.visibility = View.VISIBLE
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

package com.example.expensetracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.expensetracker.databinding.ActivityMainBinding

/**
 * Main Activity - Single Activity Architecture
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
    }

    /**
     * Setup Navigation Component with Bottom Navigation
     */
    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Setup bottom navigation with NavController
        binding.bottomNavigation.setupWithNavController(navController)

        // Custom navigation for "Add" button
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_expenses -> {
                    if (navController.currentDestination?.id != R.id.expenseListFragment) {
                        navController.navigate(R.id.expenseListFragment)
                    }
                    true
                }
                R.id.navigation_add -> {
                    // Always navigate to add screen (don't check current destination)
                    navController.navigate(R.id.addExpenseFragment)
                    false // Don't select this item in bottom nav
                }
                R.id.navigation_statistics -> {
                    if (navController.currentDestination?.id != R.id.statisticsFragment) {
                        navController.navigate(R.id.statisticsFragment)
                    }
                    true
                }
                else -> false
            }
        }

        // Update bottom navigation selection based on current destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.expenseListFragment -> {
                    binding.bottomNavigation.menu.findItem(R.id.navigation_expenses).isChecked = true
                }
                R.id.statisticsFragment -> {
                    binding.bottomNavigation.menu.findItem(R.id.navigation_statistics).isChecked = true
                }
                R.id.addExpenseFragment -> {
                    // Don't select any bottom nav item for add screen
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
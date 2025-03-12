package com.avcialper.lemur.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val vm: MainViewModel by viewModels()

    private val navController by lazy {
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navGraph.id) as NavHostFragment
        navHostFragment.navController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            val (_, isCurrentUserChecked, isThemeChecked) = vm.state.value
            val isSplashCompleted = isThemeChecked && isCurrentUserChecked
            if (isSplashCompleted) handleFlow()
            isSplashCompleted.not()
        }
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        createNavigation()
    }

    private fun handleFlow() {
        val isLoginPage = navController.currentDestination?.id == R.id.loginFragment
        val isLoggedIn = vm.state.value.user != null
        if (isLoginPage && isLoggedIn)
            navController.navigate(R.id.toMenu)
    }

    private fun createNavigation() = with(binding) {
        bottomMenu.apply {
            visibility = View.VISIBLE
            setupWithNavController(navController)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChangedListener(destination)
        }
    }

    private fun onDestinationChangedListener(destination: NavDestination) {
        val visibility =
            if (isNotBottomNavigationDestinations(destination)) View.GONE else View.VISIBLE
        binding.bottomMenu.visibility = visibility
    }

    private fun isNotBottomNavigationDestinations(destination: NavDestination): Boolean {
        val bottomViewDestinations =
            listOf(R.id.homeFragment, R.id.teamFragment, R.id.profileFragment)
        val index = bottomViewDestinations.indexOf(destination.id)
        return index == -1
    }
}
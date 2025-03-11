package com.avcialper.lemur.ui

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
            vm.isCompeted.value.not()
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
        observeUser()
    }

    private fun observeUser() = lifecycleScope.launch {
        vm.user.collect {
            it?.let {
                navController.navigate(R.id.toMenu)
            }
        }
    }

    private fun createNavigation() = with(binding) {
        bottomMenu.apply {
            visibility = View.VISIBLE
            setupWithNavController(navController)
        }
        navController.addOnDestinationChangedListener(::onDestinationChangedListener)
    }

    private fun onDestinationChangedListener(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
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
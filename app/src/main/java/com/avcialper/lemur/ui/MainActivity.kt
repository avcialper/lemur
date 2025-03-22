package com.avcialper.lemur.ui

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.avcialper.lemur.R
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.databinding.ActivityMainBinding
import com.avcialper.lemur.helper.SimplifiedAnimationListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

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
            val isSplashCompleted = vm.isThemeChecked.value && vm.isCurrentUserChecked.value
            if (isSplashCompleted) handleFlow()
            isSplashCompleted.not()
        }
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        createNavigation()
        observeInternetConnection()
    }

    private fun handleFlow() {
        val isLoginPage = navController.currentDestination?.id == R.id.loginFragment
        val isLoggedIn = vm.user.value != null
        if (isLoginPage && isLoggedIn)
            navController.navigate(R.id.toMenu)
    }

    private fun createNavigation() = with(binding) {
        bottomMenu.apply {
            visibility = View.VISIBLE
            setupWithNavController(navController)
            setOnItemSelectedListener(::onItemSelectedListener)
        }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            onDestinationChangedListener(destination)
        }
    }

    private fun onItemSelectedListener(item: MenuItem): Boolean {
        val destinationId = item.itemId
        val currentId = navController.currentDestination?.id
        return if (destinationId != currentId) {
            navController.navigate(destinationId)
            true
        } else
            false
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

    private fun observeInternetConnection() = with(binding) {
        vm.isConnected.onEach { isConnected ->
            val currentVisibility = internetStatus.visibility
            AppManager.isConnected = isConnected

            if (isConnected && currentVisibility == View.VISIBLE) {
                val slideOut = AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_out)
                internetStatus.startAnimation(slideOut)

                slideOut.setAnimationListener(object : SimplifiedAnimationListener {
                    override fun onAnimationEnd(animation: Animation?) {
                        internetStatus.visibility = View.GONE
                    }
                })

            } else if (!isConnected && currentVisibility == View.GONE) {
                internetStatus.visibility = View.VISIBLE

                val slideIn = AnimationUtils.loadAnimation(this@MainActivity, R.anim.slide_in)
                internetStatus.startAnimation(slideIn)
            }
        }.launchIn(lifecycleScope)
    }
}
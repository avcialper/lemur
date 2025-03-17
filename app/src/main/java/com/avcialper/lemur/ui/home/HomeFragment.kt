package com.avcialper.lemur.ui.home

import android.Manifest.permission.POST_NOTIFICATIONS
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.databinding.FragmentHomeBinding
import com.avcialper.lemur.helper.PermissionManager
import com.avcialper.lemur.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(FragmentHomeBinding::inflate) {

    override fun FragmentHomeBinding.initialize() {
        checkNotificationPermission()
        initUI()
    }

    private fun initUI() = with(binding) {
    }

    private fun checkNotificationPermission() {
        val permissionManager = PermissionManager(this@HomeFragment)
        if (permissionManager.isUpperTiramisu) {
            val isGranted = permissionManager.checkPermission(POST_NOTIFICATIONS)
            if (!isGranted)
                permissionManager.requestPermission(POST_NOTIFICATIONS, ::handlePermissionResult)
        }
    }

    private fun handlePermissionResult(isGranted: Boolean) {
        AppManager.deviceNotificationPermission = isGranted
    }

}
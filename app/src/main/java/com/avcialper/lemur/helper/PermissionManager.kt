package com.avcialper.lemur.helper

import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class PermissionManager(private val fragment: Fragment) {

    private var handlePermissionResult: (Boolean) -> Unit = {}
    private var handleMultiplePermissionResult: (Map<String, Boolean>) -> Unit = {}

    val isUpperTiramisu = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    val isUpperUpsideDownCake = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    private val permissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            handlePermissionResult.invoke(isGranted)
        }

    private val multiplePermissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            handleMultiplePermissionResult.invoke(results)
        }

    fun checkPermission(permission: String): Boolean {
        val statusCode = ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
        return statusCode == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(permission: String, handlePermissionResult: (Boolean) -> Unit) {
        this.handlePermissionResult = handlePermissionResult
        permissionLauncher.launch(permission)
    }

    fun requestMultiplePermission(
        permissions: Array<String>,
        handleMultiplePermissionResult: (Map<String, Boolean>) -> Unit
    ) {
        this.handleMultiplePermissionResult = handleMultiplePermissionResult
        multiplePermissionLauncher.launch(permissions)
    }

    fun haveAnyRights(permission: String): Boolean {
        return fragment.shouldShowRequestPermissionRationale(permission)
    }

}
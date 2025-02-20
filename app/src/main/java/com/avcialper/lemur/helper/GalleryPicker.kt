package com.avcialper.lemur.helper

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class GalleryPicker(activity: AppCompatActivity) {

    private val context = activity.applicationContext
    private val activityResultRegistry = activity.activityResultRegistry

    private val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE

    fun pickImageFromGallery(onCompleted: (Uri?) -> Unit) {
        val isPermissionGranted = checkPermission()
        if (!isPermissionGranted) {
            permissionRequest { isGranted ->
                if (isGranted)
                    pickImageFromGallery(onCompleted)
            }
            return
        }

        val input = PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        val contract = ActivityResultContracts.PickVisualMedia()
        val launcher = activityResultRegistry.register("contract_key", contract, onCompleted)
        launcher.launch(input)
    }

    private fun checkPermission(): Boolean =
        context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED


    private fun permissionRequest(onCompleted: (Boolean) -> Unit) {
        val contract = ActivityResultContracts.RequestPermission()
        val launcher = activityResultRegistry.register("permission_key", contract, onCompleted)
        launcher.launch(permission)
    }
}
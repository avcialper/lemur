package com.avcialper.lemur.helper

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class ImagePicker(
    private val fragment: Fragment,
    private val onImageSelected: (Uri) -> Unit
) {
    private val galleryLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { onImageSelected(it) }
        }

    private val permissionLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
            val grantedPermissions = results.filterValues { it }.keys
            val deniedPermissions = results.filterValues { !it }.keys

            // return if permissions denied
            if (grantedPermissions.isEmpty()) return@registerForActivityResult

            val isPartialAccess =
                grantedPermissions.contains("android.permission.READ_MEDIA_VISUAL_USER_SELECTED") &&
                        deniedPermissions.contains("android.permission.READ_MEDIA_IMAGES")

            if (isPartialAccess)
                selectImageFromMedia()
            else
                openGallery()
        }

    fun pickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            checkPermission(READ_MEDIA_IMAGES)
        ) {
            // Full access on Android 13 (API level 33) or higher
            openGallery()
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE &&
            checkPermission(READ_MEDIA_VISUAL_USER_SELECTED)
        ) {
            // Partial access on Android 14 (API level 34) or higher
            selectImageFromMedia()
        } else if (checkPermission(READ_EXTERNAL_STORAGE)) {
            // Full access up to Android 12 (API level 32)
            openGallery()
        } else {
            // Request permission
            requestPermission()
        }
    }

    private fun checkPermission(permission: String): Boolean {
        val statusCode = ContextCompat.checkSelfPermission(fragment.requireContext(), permission)
        return statusCode == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permissions = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> arrayOf(
                READ_MEDIA_IMAGES,
                READ_MEDIA_VISUAL_USER_SELECTED
            )

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> arrayOf(READ_MEDIA_IMAGES)
            else -> arrayOf(READ_EXTERNAL_STORAGE)
        }
        permissionLauncher.launch(permissions)
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun selectImageFromMedia() {
        // TODO foreach ile bütün resimleri gezip bir bottom sheet üzerinde göster, seçimi oradan yap
        val projection = arrayOf(MediaStore.Images.Media._ID)
        fragment.requireActivity().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                val uri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                onImageSelected.invoke(uri)
            }
        }
    }
}
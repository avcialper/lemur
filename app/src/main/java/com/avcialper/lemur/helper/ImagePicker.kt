package com.avcialper.lemur.helper

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.content.ContentUris
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.avcialper.lemur.R
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.component.imageselector.PartialImageViewer
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView

class ImagePicker(
    private val fragment: Fragment,
    private val onImageSelected: (Uri) -> Unit
) {
    private val permissionManager = PermissionManager(fragment)

    private val options = CropImageOptions(
        activityBackgroundColor = ContextCompat.getColor(fragment.requireContext(), R.color.black),
        activityTitle = ContextCompat.getString(fragment.requireContext(), R.string.crop_image),
        cropMenuCropButtonTitle = ContextCompat.getString(fragment.requireContext(), R.string.crop),
        toolbarColor = ContextCompat.getColor(fragment.requireContext(), R.color.black),
        cropShape = CropImageView.CropShape.RECTANGLE,
        scaleType = CropImageView.ScaleType.CENTER_CROP,
        aspectRatioX = 1,
        aspectRatioY = 1,
        fixAspectRatio = true
    )

    private val galleryLauncher =
        fragment.registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let { cropImage(uri) }
        }

    private val imageCropLauncher =
        fragment.registerForActivityResult(CropImageContract()) { result ->
            if (result.isSuccessful) {
                result.uriContent?.let { uri ->
                    onImageSelected.invoke(uri)
                }
            }
        }

    private fun cropImage(uri: Uri) {
        val cropOptions = CropImageContractOptions(uri, options)
        imageCropLauncher.launch(cropOptions)
    }

    fun pickImage() {
        if (permissionManager.isUpperTiramisu && permissionManager.checkPermission(READ_MEDIA_IMAGES)) {
            // Full access on Android 13 (API level 33) or higher
            openGallery()
        } else if (permissionManager.isUpperUpsideDownCake &&
            permissionManager.checkPermission(READ_MEDIA_VISUAL_USER_SELECTED)
        ) {
            // Partial access on Android 14 (API level 34) or higher
            selectFromPartialImage()
        } else if (permissionManager.checkPermission(READ_EXTERNAL_STORAGE)) {
            // Full access up to Android 12 (API level 32)
            openGallery()
        } else {
            // Request permission
            requestPermission()
        }
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
        permissionManager.requestMultiplePermission(permissions, ::handlePermissionResult)
    }

    private fun handlePermissionResult(results: Map<String, Boolean>) {
        val grantedPermissions = results.filterValues { it }.keys
        val deniedPermissions = results.filterValues { !it }.keys

        // If permissions are denied, open settings.
        checkHaveAnyRights()

        // return if permissions denied
        if (grantedPermissions.isEmpty()) return

        val isPartialAccess =
            grantedPermissions.contains("android.permission.READ_MEDIA_VISUAL_USER_SELECTED") &&
                    deniedPermissions.contains("android.permission.READ_MEDIA_IMAGES")

        if (isPartialAccess)
            selectFromPartialImage()
        else
            openGallery()
    }

    private fun checkHaveAnyRights() {
        val haveAnyRights = if (permissionManager.isUpperTiramisu)
            permissionManager.haveAnyRights(READ_MEDIA_IMAGES)
        else if (permissionManager.isUpperUpsideDownCake)
            permissionManager.haveAnyRights(READ_MEDIA_VISUAL_USER_SELECTED)
        else
            permissionManager.haveAnyRights(READ_EXTERNAL_STORAGE)

        if (haveAnyRights.not()) {
            AlertFragment(R.string.gallery_permission_denied) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", fragment.requireContext().packageName, null)
                fragment.startActivity(intent)
            }.show(fragment.childFragmentManager, "alert")
        }
    }

    private fun openGallery() {
        galleryLauncher.launch("image/*")
    }

    private fun selectFromPartialImage() {
        val projection = arrayOf(MediaStore.Images.Media._ID)

        val collectionUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Query all the device storage volumes instead of the primary only
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val uris = mutableListOf<Uri>()

        fragment.requireActivity().contentResolver.query(
            collectionUri,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"

        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (cursor.moveToNext()) {
                val uri = ContentUris.withAppendedId(collectionUri, cursor.getLong(idColumn))
                uris.add(uri)
            }
        }

        PartialImageViewer(uris) { uri ->
            cropImage(uri)
        }.show(fragment.parentFragmentManager, "image_selector")
    }
}
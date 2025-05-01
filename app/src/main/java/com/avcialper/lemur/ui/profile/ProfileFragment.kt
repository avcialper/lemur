package com.avcialper.lemur.ui.profile

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.fragment.app.viewModels
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.AppManager
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.User
import com.avcialper.lemur.databinding.FragmentProfileBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.component.themeselector.ThemeSelector
import com.avcialper.lemur.util.constant.Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val vm: ProfileViewModel by viewModels()

    override fun FragmentProfileBinding.initialize() {
        observer()
        setListeners()
        initUI()
    }

    private fun initUI() = with(binding) {
        componentNotification.updateIcon(AppManager.notificationIconId())
        componentTheme.updateIcon(AppManager.themeIconId())
        componentEmailVerify.apply {
            val iconAndLabelIds = UserManager.emailIconAndLabelIds()
            updateIconAndLabel(iconAndLabelIds.labelId, iconAndLabelIds.iconId)
        }
    }

    private fun setListeners() = with(binding) {
        componentUpdateProfile.setOnClickListener {
            ProfileFragmentDirections.toUpdateProfile().navigate()
        }
        componentUpdateEmail.setOnClickListener {
            ProfileFragmentDirections.toUpdateEmail().navigate()
        }
        componentUpdatePassword.setOnClickListener {
            ProfileFragmentDirections.toChangePassword().navigate()
        }
        componentNotification.setOnClickListener { changeNotificationPermission() }
        componentTheme.setOnClickListener { openThemeSelector() }
        componentEmailVerify.setOnClickListener { verifyEmail() }
        componentLogout.setOnClickListener { logout() }
    }

    private fun observer() = with(vm) {
        user.createObserver(::collectUser)
        theme.createObserver(::collectTheme)
        notificationPermission.createObserver(::collectNotification)
    }

    private fun collectUser(user: User?) = with(binding) {
        textUsername.text = user?.username
        textAbout.text = user?.about
        imageProfilePicture.load(user?.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.logo)
            error(R.drawable.logo)
        }
        handleEmailVerification(user?.firebaseUser?.isEmailVerified ?: false)
    }

    private fun collectNotification(isGranted: Boolean) = with(binding) {
        val iconId =
            if (isGranted) R.drawable.ic_notifications_active else R.drawable.ic_notifications_off
        if (isGranted != AppManager.isNotificationPermissionGranted) {
            componentNotification.animatedIconUpdate(iconId)
            AppManager.isNotificationPermissionGranted = isGranted
        }
    }

    private fun collectTheme(theme: Theme) = with(binding) {
        val iconId = when (theme) {
            Theme.SYSTEM_DEFAULT -> R.drawable.ic_system_default
            Theme.LIGHT -> R.drawable.ic_light_mode
            Theme.DARK -> R.drawable.ic_dark_mode
        }
        if (theme != AppManager.theme)
            componentTheme.animatedIconUpdate(iconId)
    }

    private fun changeNotificationPermission() {
        val isGranted = AppManager.isNotificationPermissionGranted.not()
        if (isGranted && AppManager.isDeviceNotificationPermissionGranted.not())
            AlertFragment(R.string.device_notification_permission_denied) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.fromParts("package", context?.packageName, null)
                startActivity(intent)
            }.show(childFragmentManager, "alert")

        vm.changeNotificationPermission()
    }

    private fun openThemeSelector() {
        ThemeSelector().show(childFragmentManager, "theme_selector")
    }

    private fun verifyEmail() {
        val isEmailVerified = UserManager.user?.firebaseUser?.isEmailVerified ?: false
        if (isEmailVerified) return

        vm.sendEmailVerification {
            val message = getString(R.string.email_sent)
            toast(message)
        }
    }

    private fun handleEmailVerification(isEmailVerified: Boolean) {
        val iconId = if (isEmailVerified) R.drawable.ic_email_verified else R.drawable.ic_email
        val labelId = if (isEmailVerified) R.string.email_verified else R.string.email_verify
        if (binding.componentEmailVerify.isSameLabel(labelId).not())
            binding.componentEmailVerify.updateIconAndLabel(labelId, iconId)
    }

    private fun logout() {
        AlertFragment(R.string.logout_message) {
            vm.logout {
                val destination = ProfileFragmentDirections.toLogin()
                destination.navigate()
            }
        }.show(childFragmentManager, "alert")
    }

    override fun onResume() {
        vm.reloadData()
        super.onResume()
    }

}
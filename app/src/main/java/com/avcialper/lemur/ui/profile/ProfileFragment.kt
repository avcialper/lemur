package com.avcialper.lemur.ui.profile

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.User
import com.avcialper.lemur.databinding.ComponentIconLabelBinding
import com.avcialper.lemur.databinding.FragmentProfileBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val vm: ProfileViewModel by viewModels()

    override fun FragmentProfileBinding.initialize() {
        observer()
        initUI()
    }

    private fun initUI() = with(binding) {
        componentUpdateProfile.init(R.string.update_profile, R.drawable.ic_edit)
        componentUpdatePassword.init(R.string.change_password, R.drawable.ic_change_password)
        componentNotification.init(R.string.notifications, R.drawable.ic_notifications_active)
        componentTheme.init(R.string.theme, R.drawable.ic_light_mode)
        componentGitHub.init(R.string.github, R.drawable.ic_github)
        componentLogout.init(R.string.logout, R.drawable.ic_logout, ::logout)
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.user.collect(::collect)
        }
    }

    private fun collect(user: User?) = with(binding) {
        textUsername.text = user?.username
        textEmail.text = user?.email
        imageProfilePicture.load(user?.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.logo)
            error(R.drawable.logo)

        }
        handleEmailVerification(user?.firebaseUser?.isEmailVerified ?: false)
    }

    private fun ComponentIconLabelBinding.init(
        label: Int,
        icon: Int,
        onClick: (() -> Unit)? = null
    ) {
        textLabel.text = getString(label)
        textLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
        textLabel.setOnClickListener { onClick?.invoke() }
    }

    private fun logout() {
        AlertFragment(R.string.logout_message) {
            vm.logout {
                val destination = ProfileFragmentDirections.toLogin()
                destination.navigate()
            }
        }.show(childFragmentManager, "alert")
    }

    private fun verifyEmail() {
        vm.sendEmailVerification {
            val message = getString(R.string.email_sent)
            toast(message)
        }
    }

    private fun handleEmailVerification(isEmailVerified: Boolean) {
        binding.componentEmailVerify.init(
            label = if (isEmailVerified) R.string.email_verified else R.string.email_verify,
            icon = if (isEmailVerified) R.drawable.ic_email_verified else R.drawable.ic_email,
            onClick = if (isEmailVerified) null else ::verifyEmail
        )
    }

    override fun onResume() {
        vm.reloadData()
        super.onResume()
    }

}
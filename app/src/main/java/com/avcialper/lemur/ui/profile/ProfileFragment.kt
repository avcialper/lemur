package com.avcialper.lemur.ui.profile

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.avcialper.lemur.R
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

    private fun initUI() {
        binding.apply {
            componentUpdateProfile.init(R.string.update_profile, R.drawable.ic_edit)
            componentUpdatePassword.init(R.string.change_password, R.drawable.ic_change_password)
            componentNotification.init(R.string.notifications, R.drawable.ic_notifications_active)
            componentTheme.init(R.string.theme, R.drawable.ic_light_mode)
            componentGitHub.init(R.string.github, R.drawable.ic_github)
            componentLogout.init(R.string.logout, R.drawable.ic_logout, ::logout)
        }
    }

    private fun observer() {
        binding.apply {
            lifecycleScope.launch {
                vm.user.collect {
                    textUsername.text = it?.username
                    textEmail.text = it?.email
                    imageProfilePicture.load(it?.imageUrl) {
                        crossfade(true)
                        placeholder(R.drawable.logo)
                        error(R.drawable.logo)
                    }

                    if (it?.firebaseUser?.isEmailVerified == true) {
                        componentEmailVerify.init(
                            R.string.email_verified,
                            R.drawable.ic_email_verified
                        )
                    } else {
                        componentEmailVerify.init(
                            R.string.email_verify,
                            R.drawable.ic_email,
                            ::verifyEmail
                        )
                    }
                }
            }
        }
    }

    private fun ComponentIconLabelBinding.init(
        label: Int,
        icon: Int,
        onClick: (() -> Unit)? = null
    ) {
        textLabel.text = getLabel(label)
        textLabel.setCompoundDrawablesRelativeWithIntrinsicBounds(icon, 0, 0, 0)
        textLabel.setOnClickListener { onClick?.invoke() }
    }

    private fun getLabel(id: Int): String {
        val context = requireContext()
        return ContextCompat.getString(context, id)
    }

    private fun logout() {
        val alert = AlertFragment(R.string.logout_message) {
            vm.logout {
                val destination = ProfileFragmentDirections.toLogin()
                destination.navigate()
            }
        }
        alert.show(childFragmentManager, "alert")
    }

    private fun verifyEmail() {
        vm.sendEmailVerification {
            val message =
                ContextCompat.getString(requireContext(), R.string.email_verification_sent)
            toast(message)
        }
    }

    override fun onResume() {
        vm.reloadData()
        super.onResume()
    }

}
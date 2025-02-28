package com.avcialper.lemur.ui.profile

import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.ComponentIconLabelBinding
import com.avcialper.lemur.databinding.FragmentProfileBinding
import com.avcialper.lemur.ui.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val vm: ProfileViewModel by viewModels()

    override fun FragmentProfileBinding.initialize() {
        initUI()
    }

    private fun initUI() {
        binding.apply {
            textUsername.text = "developerlemur"
            textEmail.text = "info.lemurapp@gmail.com"

            componentUpdateProfile.init(R.string.update_profile, R.drawable.ic_edit)
            componentUpdatePassword.init(R.string.change_password, R.drawable.ic_change_password)
            componentNotification.init(R.string.notifications, R.drawable.ic_notifications_active)
            componentTheme.init(R.string.theme, R.drawable.ic_light_mode)
            componentGitHub.init(R.string.github, R.drawable.ic_github)
            componentEmailVerify.init(R.string.email_verify, R.drawable.ic_email)
            componentLogout.init(R.string.logout, R.drawable.ic_logout)

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

}
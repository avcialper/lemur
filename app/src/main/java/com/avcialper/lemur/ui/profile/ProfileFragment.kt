package com.avcialper.lemur.ui.profile

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.User
import com.avcialper.lemur.databinding.FragmentProfileBinding
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.component.themeselector.ThemeSelector
import com.avcialper.lemur.util.constant.Theme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(FragmentProfileBinding::inflate) {

    private val vm: ProfileViewModel by viewModels()

    override fun FragmentProfileBinding.initialize() {
        observer()
        initUI()
    }

    private fun initUI() = with(binding) {
        componentTheme.setOnClickListener { openThemeSelector() }
        componentLogout.setOnClickListener { logout() }
    }

    private fun observer() = with(vm) {
        user.onEach(::collectUser).launchIn(viewLifecycleOwner.lifecycleScope)
        theme.onEach(::collectTheme).launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun collectUser(user: User?) = with(binding) {
        textUsername.text = user?.username
        textEmail.text = user?.email
        imageProfilePicture.load(user?.imageUrl) {
            crossfade(true)
            placeholder(R.drawable.logo)
            error(R.drawable.logo)
        }
        handleEmailVerification(user?.firebaseUser?.isEmailVerified ?: false)
    }

    private fun collectTheme(theme: Theme) = with(binding) {
        val iconId = when (theme) {
            Theme.SYSTEM_DEFAULT -> R.drawable.ic_system_default
            Theme.LIGHT -> R.drawable.ic_light_mode
            Theme.DARK -> R.drawable.ic_dark_mode
        }
        componentTheme.updateIcon(iconId)
    }

    private fun openThemeSelector() {
        ThemeSelector().show(childFragmentManager, "theme_selector")
    }

    private fun verifyEmail() {
        vm.sendEmailVerification {
            val message = getString(R.string.email_sent)
            toast(message)
        }
    }

    private fun handleEmailVerification(isEmailVerified: Boolean) =
        with(binding.componentEmailVerify) {
            val icon = if (isEmailVerified) R.drawable.ic_email_verified else R.drawable.ic_email
            val label = if (isEmailVerified) R.string.email_verified else R.string.email_verify
            val onClick = if (isEmailVerified) null else ::verifyEmail

            if (isDifferent(label))
                updateAll(label, icon, onClick)
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
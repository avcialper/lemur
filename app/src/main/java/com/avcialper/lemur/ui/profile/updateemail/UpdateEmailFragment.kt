package com.avcialper.lemur.ui.profile.updateemail

import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.databinding.FragmentUpdateEmailBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.NotSameRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateEmailFragment :
    BaseFragment<FragmentUpdateEmailBinding>(FragmentUpdateEmailBinding::inflate) {

    private val vm: UpdateEmailViewModel by viewModels()

    override fun FragmentUpdateEmailBinding.initialize() {
        setListeners()
        observer()
        binding.componentEmail.value = UserManager.user?.email ?: getString(R.string.email)
    }

    private fun setListeners() = with(binding) {
        buttonUpdate.setOnClickListener {
            if (validate())
                vm.updateEmail(componentEmail.value, componentPassword.value)
        }
    }

    private fun observer() = with(vm) {
        state.createResourceObserver(::handleSuccess, ::loadingState, ::handleError)
        isUpdated.createObserver(::handleIsUpdate)
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        componentEmail.setLoadingState(isLoading)
        componentPassword.setLoadingState(isLoading)
        buttonUpdate.updateLoadingState(isLoading)
    }

    private fun handleError(errorMessage: String) {
        if (errorMessage == getString(R.string.error_invalid_credential))
            toast(R.string.error_wrong_password)
        else
            toast(errorMessage)
    }

    private fun handleSuccess() {
        // After email was updated, show alert dialog for checking email
        AlertFragment(R.string.check_email, false).show(childFragmentManager, "alert")
    }

    private fun handleIsUpdate(isUpdated: Boolean?) {
        // If email was updated, navigate back
        if (isUpdated == true)
            goBack()
    }

    private fun validate(): Boolean = with(binding) {
        val isValidEmail = componentEmail.validate(
            rules = listOf(
                EmptyRule(),
                EmailRule(),
                NotSameRule(componentEmail.value, R.string.is_same_email)
            )
        )

        val minPasswordLength = getInt(R.integer.min_password_length)
        val maxPasswordLength = getInt(R.integer.max_password_length)
        val isValidPassword = componentPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minPasswordLength, maxPasswordLength),
                PasswordRule()
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.password,
                    minPasswordLength,
                    maxPasswordLength
                )
            }
        )

        return isValidEmail && isValidPassword
    }

    override fun onResume() {
        super.onResume()
        // If an email change email was sent, log in after the user returns to the app
        if (vm.isUpdated.value != null)
            vm.login(binding.componentEmail.value, binding.componentPassword.value)
    }

}
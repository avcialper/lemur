package com.avcialper.lemur.ui.profile.updatepassword

import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentUpdatePasswordBinding
import com.avcialper.lemur.helper.validator.ConfirmPasswordRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.NotSameRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.util.extension.exceptionConverter
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdatePasswordFragment : BaseFragment<FragmentUpdatePasswordBinding>(
    FragmentUpdatePasswordBinding::inflate
) {

    private val vm: UpdatePasswordViewModel by viewModels()

    override fun FragmentUpdatePasswordBinding.initialize() {
        setListeners()
        observer()
    }

    private fun setListeners() = with(binding) {
        buttonUpdate.setOnClickListener {
            if (validate())
                vm.updatePassword(inputCurrentPassword.value, inputNewPassword.value)
        }
    }

    private fun observer() {
        vm.state.createResourceObserver(
            ::handleSuccess,
            ::loadingState,
            handleException = ::handleError
        )
    }


    private fun handleError(e: Exception) {
        val errorMessage = if (e is FirebaseAuthInvalidCredentialsException)
            getString(R.string.error_wrong_password)
        else
            requireContext().exceptionConverter(e)
        toast(errorMessage)
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        inputCurrentPassword.setLoadingState(isLoading)
        inputNewPassword.setLoadingState(isLoading)
        inputNewPasswordConfirm.setLoadingState(isLoading)
        buttonUpdate.updateLoadingState(isLoading)
    }

    private fun handleSuccess() {
        goBack()
    }

    private fun validate(): Boolean = with(binding) {
        val minPasswordLength = getInt(R.integer.min_password_length)
        val maxPasswordLength = getInt(R.integer.max_password_length)
        val isValidCurrentPassword = inputCurrentPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minPasswordLength, maxPasswordLength),
                PasswordRule()
            ),
            formatErrorMessage = ::formatErrorMessage
        )

        val isValidNewPassword = inputNewPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minPasswordLength, maxPasswordLength),
                PasswordRule(),
                NotSameRule(inputCurrentPassword.value)
            ),
            formatErrorMessage = ::formatErrorMessage
        )

        val isValidNewPasswordConfirm = inputNewPasswordConfirm.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minPasswordLength, maxPasswordLength),
                PasswordRule(),
                ConfirmPasswordRule(inputNewPassword.value)
            ),
            formatErrorMessage = ::formatErrorMessage
        )

        return isValidCurrentPassword && isValidNewPassword && isValidNewPasswordConfirm
    }

    private fun formatErrorMessage(errorMessage: String): String {
        val minPasswordLength = getInt(R.integer.min_password_length)
        val maxPasswordLength = getInt(R.integer.max_password_length)

        return errorMessage.formatInvalidLengthError(
            requireContext(),
            R.string.password,
            minPasswordLength,
            maxPasswordLength
        )
    }
}
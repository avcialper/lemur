package com.avcialper.lemur.ui.profile.updatepassword

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentUpdatePasswordBinding
import com.avcialper.lemur.helper.validator.ConfirmPasswordRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.NotSameRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.exceptionConverter
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UpdatePasswordFragment : BaseFragment<FragmentUpdatePasswordBinding>(
    FragmentUpdatePasswordBinding::inflate
) {

    private val vm: UpdatePasswordViewModel by viewModels()

    override fun FragmentUpdatePasswordBinding.initialize() {
        initUI()
        setupListener()
        observer()
    }

    private fun initUI() = with(binding) {
        inputCurrentPassword.value = vm.currentPassword.value
        inputNewPassword.value = vm.newPassword.value
        inputNewPasswordConfirm.value = vm.newPasswordConfirm.value
    }

    private fun setupListener() = with(binding) {
        inputCurrentPassword.onTextChanged(vm::onCurrentPasswordChanged)
        inputNewPassword.onTextChanged(vm::onNewPasswordChanged)
        inputNewPasswordConfirm.onTextChanged(vm::onNewPasswordConfirmChanged)
        buttonUpdate.setOnClickListener {
            if (validate())
                vm.updatePassword()
        }
    }

    private fun observer() {
        vm.state.onEach(::handleResource).launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun handleResource(resource: Resource<Boolean>?) {
        when (resource) {
            is Resource.Error -> handleError(resource.throwable!!)
            is Resource.Loading -> loadingState(true)
            is Resource.Success -> handleSuccess()
            null -> Unit
        }
    }

    private fun handleError(e: Exception) {
        val errorMessage = if (e is FirebaseAuthInvalidCredentialsException)
            getString(R.string.error_wrong_password)
        else
            requireContext().exceptionConverter(e)
        toast(errorMessage)
        loadingState(false)
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        inputCurrentPassword.setLoadingState(isLoading)
        inputNewPassword.setLoadingState(isLoading)
        inputNewPasswordConfirm.setLoadingState(isLoading)
        buttonUpdate.updateLoadingState(isLoading)
    }

    private fun handleSuccess() {
        loadingState(false)
        findNavController().popBackStack()
    }

    private fun validate(): Boolean = with(binding) {
        val isValidCurrentPassword = inputCurrentPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(),
                PasswordRule()
            )
        )

        val isValidNewPassword = inputNewPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(),
                PasswordRule(),
                NotSameRule(inputCurrentPassword.value),
            )
        )

        val isValidNewPasswordConfirm = inputNewPasswordConfirm.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(),
                PasswordRule(),
                ConfirmPasswordRule(inputNewPassword.value),
            )
        )

        return isValidCurrentPassword && isValidNewPassword && isValidNewPasswordConfirm
    }

}
package com.avcialper.lemur.ui.auth.login

import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentLoginBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.auth.AuthBaseFragment
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : AuthBaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val vm: LoginViewModel by viewModels()

    override fun FragmentLoginBinding.initialize() {
        observer()
        setListeners()
    }

    private fun setListeners() = with(binding) {
        textSignup.setOnClickListener { LoginFragmentDirections.toSignup().navigate() }
        textForgotPassword.setOnClickListener { LoginFragmentDirections.toForgot().navigate() }

        buttonLogin.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.onLoginClicked(inputEmail.value, inputPassword.value)
        }
    }

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::loadingState)
    }

    private fun handleSuccess() {
        val direction = LoginFragmentDirections.toHome()
        direction.navigate()
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        imageAppLogo.alpha = if (isLoading) 0.5f else 1f
        inputEmail.setLoadingState(isLoading)
        inputPassword.setLoadingState(isLoading)
        buttonLogin.updateLoadingState(isLoading)
        textForgotPassword.updateLoadingState(isLoading)
        textSignup.updateLoadingState(isLoading)
    }

    override fun validate(): Boolean = with(binding) {
        val isValidEmail = inputEmail.validate(
            rules = listOf(
                EmptyRule(),
                EmailRule()
            )
        )

        val isValidPassword = inputPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(Constants.MIN_PASSWORD_LENGTH, Constants.MAX_PASSWORD_LENGTH),
                PasswordRule()
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.password,
                    Constants.MIN_PASSWORD_LENGTH,
                    Constants.MAX_PASSWORD_LENGTH
                )
            }
        )

        return isValidEmail && isValidPassword
    }

}
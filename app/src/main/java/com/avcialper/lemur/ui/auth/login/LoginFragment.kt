package com.avcialper.lemur.ui.auth.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDirections
import com.avcialper.lemur.databinding.FragmentLoginBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.auth.AuthBaseFragment
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.exceptionConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : AuthBaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val vm: LoginViewModel by viewModels()

    override fun FragmentLoginBinding.initialize() {
        observer()
        restore()
        setupListeners()
    }

    private fun setupListeners() = with(binding) {
        inputEmail.onTextChanged(vm::onEmailChanged)
        inputPassword.onTextChanged(vm::onPasswordChanged)

        textSignup.setOnClickListener { handleNavigation(LoginFragmentDirections.toSignup()) }
        textForgotPassword.setOnClickListener { handleNavigation(LoginFragmentDirections.toForgot()) }

        buttonLogin.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.onLoginClicked()
        }
    }

    private fun handleNavigation(direction: NavDirections) {
        direction.navigate()
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collect { loginState -> handleResource(loginState.resource) }
        }
    }

    private fun handleResource(resource: Resource<Boolean>?) {
        when (resource) {
            is Resource.Error -> handleError(resource.throwable!!)
            is Resource.Loading -> loadingState(true)
            is Resource.Success -> handleSuccess()
            null -> Unit
        }
    }

    private fun handleSuccess() {
        loadingState(false)
        val direction = LoginFragmentDirections.toMenu()
        direction.navigate()
    }

    private fun handleError(e: Exception) {
        val errorMessage = requireContext().exceptionConverter(e)
        toast(errorMessage)
        loadingState(false)
        vm.clearError()
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        inputEmail.setLoadingState(isLoading)
        inputPassword.setLoadingState(isLoading)
        buttonLogin.updateLoadingState(isLoading)
        textForgotPassword.updateLoadingState(isLoading)
        textSignup.updateLoadingState(isLoading)
    }

    private fun restore() = with(binding) {
        inputEmail.value = vm.state.value.email
        inputPassword.value = vm.state.value.password
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
                LengthRule(),
                PasswordRule()
            )
        )

        return isValidEmail && isValidPassword
    }

}
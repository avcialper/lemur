package com.avcialper.lemur.ui.auth.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
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

        if (vm.isLoggedIn.value) {
            val direction = LoginFragmentDirections.toProfile()
            direction.navigate()
        }

        observer()
        restore()
        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {
            inputEmail.onTextChanged(vm::onEmailChanged)
            inputPassword.onTextChanged(vm::onPasswordChanged)

            textSignup.setOnClickListener {
                val direction = LoginFragmentDirections.toSignup()
                direction.navigate()
            }

            textForgotPassword.setOnClickListener {
                val direction = LoginFragmentDirections.toForgot()
                direction.navigate()
            }

            buttonLogin.setOnClickListener {
                val isValid = validate()
                if (isValid)
                    vm.onLoginClicked()
            }
        }
    }

    private fun observer() {
        lifecycleScope.launch {
            vm.state.collect { loginState ->
                when (loginState.resource) {
                    is Resource.Loading -> {
                        loadingState(true)
                    }

                    is Resource.Error -> {
                        val errorMessage =
                            requireContext().exceptionConverter(loginState.resource.throwable!!)
                        toast(errorMessage)
                        loadingState(false)
                        vm.clearError()
                    }

                    is Resource.Success -> {
                        loadingState(false)
                        val direction = LoginFragmentDirections.toProfile()
                        direction.navigate()
                    }
                    // Starting state
                    null -> Unit
                }
            }
        }
    }

    private fun loadingState(isLoading: Boolean) {
        binding.apply {
            inputEmail.setLoadingState(isLoading)
            inputPassword.setLoadingState(isLoading)
            buttonLogin.updateLoadingState(isLoading)
            textForgotPassword.updateLoadingState(isLoading)
            textSignup.updateLoadingState(isLoading)
        }
    }

    private fun restore() {
        binding.apply {
            inputEmail.value = vm.state.value.email
            inputPassword.value = vm.state.value.password
        }
    }

    override fun validate(): Boolean {
        binding.apply {
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

}
package com.avcialper.lemur.ui.auth.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.avcialper.lemur.databinding.FragmentLoginBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.util.constants.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val vm by viewModels<LoginViewModel>()

    override fun FragmentLoginBinding.initialize() {
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
                        toast("Loading")
                        loadingState(true)
                    }

                    is Resource.Error -> {
                        toast("Error: ${loginState.resource.throwable}")
                        loadingState(false)
                    }

                    is Resource.Success -> {
                        toast("Success: ${loginState.resource.data}")
                        loadingState(false)
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
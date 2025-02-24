package com.avcialper.lemur.ui.auth.login

import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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

    private val viewModel by viewModels<LoginViewModel>()

    override fun FragmentLoginBinding.initialize() {
        viewModelObserver()

        binding.apply {

            inputEmail.setOnTextChangedListener(viewModel::onEmailChanged)
            inputPassword.setOnTextChangedListener(viewModel::onPasswordChanged)

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
                if (!isValid) return@setOnClickListener
                viewModel.onLoginClicked()
            }
        }
    }

    private fun viewModelObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is Resource.Loading -> {
                            loadingState(true)
                            toast("Loading")
                        }

                        is Resource.Success -> {
                            loadingState(false)
                            toast("Success")
                        }

                        is Resource.Error -> {
                            loadingState(false)
                            toast("Error: ${state.throwable}")
                        }

                        else -> Unit
                    }
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
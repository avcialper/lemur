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

    private val viewModel by viewModels<LoginViewModel>()

    override fun FragmentLoginBinding.initialize() {

        viewModelObserver()

        binding.apply {
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

                val email = inputEmail.value
                val password = inputPassword.value
                viewModel.login(email, password)
            }
        }
    }

    private fun viewModelObserver() {
        lifecycleScope.launch {
            viewModel.state.collect { state ->
                when (state) {
                    is Resource.Loading -> {
                        toast("Loading")
                    }

                    is Resource.Success -> {
                        toast("Success")
                    }

                    is Resource.Error -> {
                        toast("Error: ${state.throwable.toString()}")
                    }

                    else -> {}
                }
            }
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
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
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LoginFragment : AuthBaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val vm: LoginViewModel by viewModels()

    override fun FragmentLoginBinding.initialize() {
        observer()
        restore()
        setListeners()
    }

    private fun setListeners() = with(binding) {
        inputEmail.onTextChanged(vm::onEmailChanged)
        inputPassword.onTextChanged(vm::onPasswordChanged)

        textSignup.setOnClickListener { LoginFragmentDirections.toSignup().navigate() }
        textForgotPassword.setOnClickListener { LoginFragmentDirections.toForgot().navigate() }

        buttonLogin.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.onLoginClicked()
        }
    }

    private fun observer() {
        vm.state.onEach(::handleResource).launchIn(viewLifecycleOwner.lifecycleScope)
    }

    // Handle resource state and animatedUpdate UI accordingly
    private fun handleResource(resource: Resource<FirebaseUser>?) {
        when (resource) {
            is Resource.Error -> handleError(resource.throwable!!)
            is Resource.Loading -> loadingState(true)
            is Resource.Success -> handleSuccess()
            null -> loadingState(false)
        }
    }

    private fun handleSuccess() {
        loadingState(false)
        val direction = LoginFragmentDirections.toHome()
        direction.navigate()
    }

    private fun handleError(e: Exception) {
        val errorMessage = requireContext().exceptionConverter(e)
        toast(errorMessage)
        loadingState(false)
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        imageAppLogo.alpha = if (isLoading) 0.5f else 1f
        inputEmail.setLoadingState(isLoading)
        inputPassword.setLoadingState(isLoading)
        buttonLogin.updateLoadingState(isLoading)
        textForgotPassword.updateLoadingState(isLoading)
        textSignup.updateLoadingState(isLoading)
    }

    private fun restore() = with(binding) {
        inputEmail.value = vm.email.value
        inputPassword.value = vm.password.value
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
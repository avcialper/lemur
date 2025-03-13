package com.avcialper.lemur.ui.auth.forgot

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentForgotPasswordBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.ui.auth.AuthBaseFragment
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.exceptionConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ForgotPasswordFragment : AuthBaseFragment<FragmentForgotPasswordBinding>(
    FragmentForgotPasswordBinding::inflate
) {

    private val vm: ForgotPasswordViewModel by viewModels()

    override fun FragmentForgotPasswordBinding.initialize() {
        setupListeners()
        restore()
        observer()
    }

    override fun validate(): Boolean {
        val isValidEmail = binding.inputEmail.validate(
            rules = listOf(
                EmptyRule(),
                EmailRule()
            )
        )
        return isValidEmail
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collect(::handleResource)
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
        val message = getString(R.string.email_sent)
        toast(message)
        findNavController().popBackStack()
    }

    private fun handleError(e: Exception) {
        loadingState(false)
        val errorMessage = requireContext().exceptionConverter(e)
        toast(errorMessage)
    }

    private fun setupListeners() = with(binding) {
        inputEmail.onTextChanged(vm::onEmailChanged)
        buttonSend.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.sendPasswordResetEmail()
        }
    }

    private fun restore() {
        binding.inputEmail.value = vm.email.value
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        inputEmail.setLoadingState(isLoading)
        buttonSend.updateLoadingState(isLoading)
    }

}
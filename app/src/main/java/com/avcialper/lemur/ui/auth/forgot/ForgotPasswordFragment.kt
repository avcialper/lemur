package com.avcialper.lemur.ui.auth.forgot

import androidx.core.content.ContextCompat
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
        lifecycleScope.launch {
            vm.state.collect { state ->
                when (state.resource) {
                    is Resource.Error -> {
                        loadingState(false)
                        val errorMessage =
                            requireContext().exceptionConverter(state.resource.throwable!!)
                        toast(errorMessage)
                        vm.clearError()
                    }

                    is Resource.Loading -> loadingState(true)

                    is Resource.Success -> {
                        loadingState(false)
                        val message = ContextCompat.getString(requireContext(), R.string.email_sent)
                        toast(message)
                        findNavController().popBackStack()
                    }

                    null -> Unit
                }
            }
        }
    }

    private fun setupListeners() {
        binding.apply {
            inputEmail.onTextChanged(vm::onEmailChanged)

            buttonSend.setOnClickListener {
                val isValid = validate()
                if (isValid)
                    vm.sendPasswordResetEmail()
            }
        }
    }

    private fun restore() {
        binding.inputEmail.value = vm.state.value.email
    }

    private fun loadingState(isLoading: Boolean) {
        binding.apply {
            inputEmail.setLoadingState(isLoading)
            buttonSend.updateLoadingState(isLoading)
        }
    }

}
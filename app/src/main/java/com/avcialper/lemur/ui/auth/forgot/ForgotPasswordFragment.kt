package com.avcialper.lemur.ui.auth.forgot

import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentForgotPasswordBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.ui.auth.AuthBaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : AuthBaseFragment<FragmentForgotPasswordBinding>(
    FragmentForgotPasswordBinding::inflate
) {

    private val vm: ForgotPasswordViewModel by viewModels()

    override fun FragmentForgotPasswordBinding.initialize() {
        setListeners()
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
        vm.state.createResourceObserver(::handleSuccess, ::loadingState)
    }

    private fun handleSuccess() {
        val message = getString(R.string.email_sent)
        toast(message)
        goBack()
    }

    private fun setListeners() = with(binding) {
        buttonSend.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.sendPasswordResetEmail(inputEmail.value)
        }
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        inputEmail.setLoadingState(isLoading)
        buttonSend.updateLoadingState(isLoading)
    }

}
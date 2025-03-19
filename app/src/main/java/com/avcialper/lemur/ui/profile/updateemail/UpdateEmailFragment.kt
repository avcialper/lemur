package com.avcialper.lemur.ui.profile.updateemail

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentUpdateEmailBinding
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.exceptionConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class UpdateEmailFragment :
    BaseFragment<FragmentUpdateEmailBinding>(FragmentUpdateEmailBinding::inflate) {

    private val vm: UpdateEmailViewModel by viewModels()

    override fun FragmentUpdateEmailBinding.initialize() {
        initUI()
        setListeners()
        observer()
    }

    private fun initUI() = with(binding) {
        componentEmail.value = vm.email.value
        componentPassword.value = vm.password.value
    }

    private fun setListeners() = with(binding) {
        componentEmail.onTextChanged(vm::onEmailChanged)
        componentPassword.onTextChanged(vm::onPasswordChanged)
        buttonUpdate.setOnClickListener {
            if (validate())
                vm.updateEmail()
        }
    }

    private fun observer() = with(vm) {
        state.createObserver(::handleResource)
        isUpdated.createObserver(::handleIsUpdate)
    }

    private fun handleResource(resource: Resource<Boolean>?) {
        when (resource) {
            is Resource.Error -> handleError(resource.throwable!!)
            is Resource.Loading -> loadingState(true)
            is Resource.Success -> handleSuccess()
            null -> Unit
        }
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        componentEmail.setLoadingState(isLoading)
        componentPassword.setLoadingState(isLoading)
        buttonUpdate.updateLoadingState(isLoading)
    }

    private fun handleError(e: Exception) {
        val message = requireContext().exceptionConverter(e)
        if (message == getString(R.string.error_invalid_credential))
            toast(R.string.error_wrong_password)
        else
            toast(message)
        loadingState(false)
    }

    private fun handleSuccess() {
        loadingState(false)
        // After email was updated, show alert dialog for checking email
        AlertFragment(R.string.check_email, false).show(childFragmentManager, "alert")
    }

    private fun handleIsUpdate(isUpdated: Boolean?) {
        // If email was updated, navigate back
        if (isUpdated == true)
            findNavController().navigateUp()
    }

    private fun validate(): Boolean = with(binding) {
        val isValidEmail = componentEmail.validate(
            rules = listOf(
                EmptyRule(),
                EmailRule()
            )
        )

        val isValidPassword = componentPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(),
                PasswordRule()
            )
        )

        return isValidEmail && isValidPassword
    }

    private fun <T> Flow<T>.createObserver(action: (T) -> Unit) {
        onEach(action).launchIn(viewLifecycleOwner.lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        // If an email change email was sent, log in after the user returns to the app
        if (vm.isUpdated.value != null)
            vm.login()
    }

}
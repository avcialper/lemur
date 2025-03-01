package com.avcialper.lemur.ui.auth.signup

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.avcialper.lemur.databinding.FragmentSignupBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.UriToFile
import com.avcialper.lemur.helper.validator.ConfirmPasswordRule
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.auth.AuthBaseFragment
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.exceptionConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class SignupFragment : AuthBaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

    private lateinit var imagePicker: ImagePicker

    private val vm: SignupViewModel by viewModels()

    override fun FragmentSignupBinding.initialize() {
        imagePicker = ImagePicker(this@SignupFragment) { uri ->
            binding.imageUser.setImageURI(uri)
            vm.onImageChanged(uri)
        }

        observer()
        restore()
        setupListeners()
    }

    private fun setupListeners() = with(binding) {

        inputUsername.onTextChanged(vm::onUsernameChanged)
        inputEmail.onTextChanged(vm::onEmailChanged)
        inputPassword.onTextChanged(vm::onPasswordChanged)
        inputConfirmPassword.onTextChanged(vm::onConfirmPasswordChanged)

        imageUser.setOnClickListener {
            imagePicker.pickImage()
        }

        buttonSignup.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.onSignupClicked(::convert)
        }
    }

    private fun convert(): File = with(vm.state.value) {
        return UriToFile(requireContext()).convert(username, imageUri!!)
    }

    private fun observer() {
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { signupState -> handleResource(signupState.resource) }
        }
    }

    private fun handleResource(resource: Resource<Boolean>?) {
        when (resource) {
            is Resource.Loading -> loadingState(true)
            is Resource.Success -> handleSuccess()
            is Resource.Error -> handleError(resource.throwable!!)
            null -> Unit
        }
    }

    private fun handleSuccess() {
        loadingState(false)
        findNavController().popBackStack()
    }

    private fun handleError(e: Exception) {
        val errorMessage = requireContext().exceptionConverter(e)
        toast(errorMessage)
        loadingState(false)
        vm.clearError()
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        inputUsername.setLoadingState(isLoading)
        inputEmail.setLoadingState(isLoading)
        inputPassword.setLoadingState(isLoading)
        inputConfirmPassword.setLoadingState(isLoading)
        imageUser.updateLoadingState(isLoading)
        buttonSignup.updateLoadingState(isLoading)
    }

    private fun restore() = with(binding) {
        val (username, email, password, confirmPassword, imageUri, _) = vm.state.value

        imageUser.setImageURI(imageUri)
        inputUsername.value = username
        inputEmail.value = email
        inputPassword.value = password
        inputConfirmPassword.value = confirmPassword
    }

    override fun validate(): Boolean = with(binding) {
        val isValidUsername = inputUsername.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(4, 16)
            )
        )

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

        val isValidConfirmPassword = inputConfirmPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(),
                PasswordRule(),
                ConfirmPasswordRule(inputPassword.value)
            )
        )

        return isValidUsername && isValidEmail && isValidPassword && isValidConfirmPassword
    }
}
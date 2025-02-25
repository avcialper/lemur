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
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.util.constants.Resource
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.io.File

@AndroidEntryPoint
class SignupFragment : BaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

    private lateinit var imagePicker: ImagePicker

    private val vm by viewModels<SignupViewModel>()

    override fun FragmentSignupBinding.initialize() {
        imagePicker = ImagePicker(this@SignupFragment) { uri ->
            binding.imageUser.setImageURI(uri)
            vm.onImageChanged(uri)
        }

        observer()
        restore()
        setupListeners()
    }

    private fun setupListeners() {
        binding.apply {

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
    }

    private fun convert(): File {
        val username = vm.state.value.username
        val imageUri = vm.state.value.imageUri!!
        return UriToFile(requireContext()).convert(username, imageUri)
    }

    private fun observer() {
        lifecycleScope.launch {
            vm.state.collect { signupState ->
                when (signupState.resource) {
                    is Resource.Loading -> {
                        loadingState(true)
                    }

                    is Resource.Success -> {
                        loadingState(false)
                        findNavController().popBackStack()
                    }

                    is Resource.Error -> {
                        loadingState(false)
                        toast("Error: ${signupState.resource.throwable}")
                    }

                    else -> Unit
                }
            }
        }
    }

    private fun loadingState(isLoading: Boolean) {
        binding.apply {
            inputUsername.setLoadingState(isLoading)
            inputEmail.setLoadingState(isLoading)
            inputPassword.setLoadingState(isLoading)
            inputConfirmPassword.setLoadingState(isLoading)
            imageUser.updateLoadingState(isLoading)
            buttonSignup.updateLoadingState(isLoading)
        }
    }

    private fun restore() {
        binding.apply {
            val (username, email, password, confirmPassword, imageUri, _) = vm.state.value

            imageUser.setImageURI(imageUri)
            inputUsername.value = username
            inputEmail.value = email
            inputPassword.value = password
            inputConfirmPassword.value = confirmPassword
        }
    }

    override fun validate(): Boolean {
        binding.apply {
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
}
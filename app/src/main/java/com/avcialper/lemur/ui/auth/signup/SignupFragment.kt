package com.avcialper.lemur.ui.auth.signup

import android.net.Uri
import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentSignupBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.validator.ConfirmPasswordRule
import com.avcialper.lemur.helper.validator.EmailRule
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.PasswordRule
import com.avcialper.lemur.ui.auth.AuthBaseFragment
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class SignupFragment : AuthBaseFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate) {

    private lateinit var imagePicker: ImagePicker

    private val vm: SignupViewModel by viewModels()
    private var imageUri: Uri? = null

    override fun FragmentSignupBinding.initialize() {
        imagePicker = ImagePicker(this@SignupFragment) { uri ->
            binding.imageUser.setImageURI(uri)
            imageUri = uri
        }

        observer()
        setListeners()
    }

    private fun setListeners() = with(binding) {
        imageUser.setOnClickListener {
            imagePicker.pickImage()
        }

        buttonSignup.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.onSignupClicked(
                    inputUsername.value,
                    inputEmail.value,
                    inputPassword.value,
                    imageUri,
                    ::convert
                )
        }
    }

    private fun convert(): File = imageUri!!.convertFile()

    private fun observer() {
        vm.state.createResourceObserver(::handleSuccess, ::loadingState)
    }

    private fun handleSuccess() {
        goBack()
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        inputUsername.setLoadingState(isLoading)
        inputEmail.setLoadingState(isLoading)
        inputPassword.setLoadingState(isLoading)
        inputConfirmPassword.setLoadingState(isLoading)
        imageUser.updateLoadingState(isLoading)
        buttonSignup.updateLoadingState(isLoading)
    }

    override fun validate(): Boolean = with(binding) {
        val minUsernameLength = getInt(R.integer.min_username_length)
        val maxUsernameLength = getInt(R.integer.max_username_length)
        val isValidUsername = inputUsername.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minUsernameLength, maxUsernameLength)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.username,
                    minUsernameLength,
                    maxUsernameLength
                )
            }
        )

        val isValidEmail = inputEmail.validate(
            rules = listOf(
                EmptyRule(),
                EmailRule()
            )
        )

        val minPasswordLength = getInt(R.integer.min_password_length)
        val maxPasswordLength = getInt(R.integer.max_password_length)
        val isValidPassword = inputPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minPasswordLength, maxPasswordLength),
                PasswordRule()
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.password,
                    minPasswordLength,
                    maxPasswordLength
                )
            }
        )

        val isValidConfirmPassword = inputConfirmPassword.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(minPasswordLength, maxPasswordLength),
                PasswordRule(),
                ConfirmPasswordRule(inputPassword.value)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.password,
                    minPasswordLength,
                    maxPasswordLength
                )
            }
        )

        return isValidUsername && isValidEmail && isValidPassword && isValidConfirmPassword
    }
}
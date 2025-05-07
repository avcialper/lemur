package com.avcialper.lemur.ui.profile.updateprofile

import android.net.Uri
import androidx.fragment.app.viewModels
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.databinding.FragmentUpdateProfileBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.MaxLengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.ImageUpdateSheet
import com.avcialper.lemur.util.constant.Constants
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UpdateProfileFragment : BaseFragment<FragmentUpdateProfileBinding>(
    FragmentUpdateProfileBinding::inflate
) {

    private lateinit var imagePicker: ImagePicker

    private val vm: UpdateProfileViewModel by viewModels()
    private var imageUrl = UserManager.user?.imageUrl
    private var imageUri: Uri? = null

    override fun FragmentUpdateProfileBinding.initialize() {
        imagePicker = ImagePicker(this@UpdateProfileFragment) { uri ->
            binding.imageProfilePicture.setImageURI(uri)
            imageUri = uri
        }

        initUI()
        setListeners()
        observer()
    }

    private fun initUI() = with(binding) {
        val image = UserManager.user?.imageUrl
        loadImage(image)
        inputUsername.value = UserManager.user?.username ?: ""
        inputAbout.value = UserManager.user?.about ?: ""
    }

    private fun setListeners() = with(binding) {
        imageProfilePicture.setOnClickListener {
            ImageUpdateSheet(::deleteImage, imagePicker::pickImage).show(
                parentFragmentManager,
                "selector"
            )
        }
        buttonUpdate.setOnClickListener {
            val isValid = validate()
            if (isValid) vm.update(
                inputUsername.value,
                inputAbout.value,
                imageUrl,
                imageUri,
                ::convert
            )
        }
    }

    private fun observer() = with(vm) {
        state.createResourceObserver(::handleSuccess, ::loadingState)
    }

    private fun handleSuccess() {
        goBack()
    }

    private fun loadImage(image: Any?) {
        binding.imageProfilePicture.load(image) {
            crossfade(true)
            placeholder(R.drawable.logo)
            error(R.drawable.logo)
        }
    }

    private fun loadingState(isLoading: Boolean) = with(binding) {
        imageProfilePicture.updateLoadingState(isLoading)
        inputUsername.setLoadingState(isLoading)
        inputAbout.setLoadingState(isLoading)
        buttonUpdate.updateLoadingState(isLoading)
    }

    private fun deleteImage() {
        loadImage(null)
        imageUri = null
        imageUrl = null
    }

    private fun validate(): Boolean = with(binding) {
        val isValidUsername = inputUsername.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(Constants.MIN_USERNAME_LENGTH, Constants.MAX_USERNAME_LENGTH)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.username,
                    Constants.MIN_USERNAME_LENGTH,
                    Constants.MAX_USERNAME_LENGTH
                )
            }
        )

        val isValidAbout = inputAbout.validate(
            rules = listOf(
                MaxLengthRule(100)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(requireContext(), R.string.about, 100)
            }
        )

        return isValidUsername && isValidAbout
    }

    private fun convert(): File = imageUri!!.convertFile()

}
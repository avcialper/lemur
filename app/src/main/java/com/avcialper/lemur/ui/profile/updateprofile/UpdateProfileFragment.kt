package com.avcialper.lemur.ui.profile.updateprofile

import android.net.Uri
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.databinding.FragmentUpdateProfileBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.UriToFile
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.LengthRule
import com.avcialper.lemur.helper.validator.MaxLengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.ProfilePictureUpdateSheet
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.exceptionConverter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.io.File

@AndroidEntryPoint
class UpdateProfileFragment : BaseFragment<FragmentUpdateProfileBinding>(
    FragmentUpdateProfileBinding::inflate
) {

    private lateinit var imagePicker: ImagePicker

    private val vm: UpdateProfileViewModel by viewModels()

    override fun FragmentUpdateProfileBinding.initialize() {
        imagePicker = ImagePicker(this@UpdateProfileFragment) { uri ->
            binding.imageProfilePicture.setImageURI(uri)
            vm.onImageChanged(uri)
        }

        initUI()
        setupListeners()
        observer()
    }

    private fun initUI() = with(binding) {
        val image = vm.imageUri.value ?: vm.imageUrl.value
        loadImage(image)
        inputUsername.value = vm.username.value!!
        inputAbout.value = vm.email.value!!
    }

    private fun setupListeners() = with(binding) {
        imageProfilePicture.setOnClickListener {
            ProfilePictureUpdateSheet(::deleteImage, imagePicker::pickImage)
                .show(parentFragmentManager, "selector")
        }
        inputUsername.onTextChanged(vm::onUsernameChanged)
        inputAbout.onTextChanged(vm::onAboutChanged)
        buttonUpdate.setOnClickListener {
            val isValid = validate()
            if (isValid)
                vm.update(::convert)
        }
    }

    private fun observer() = with(vm) {
        state.createObserver(::handleResource)
        imageUri.createObserver(::imageObserver)
    }

    private fun handleResource(resource: Resource<Boolean>?) {
        when (resource) {
            is Resource.Error -> handleError(resource.throwable!!)
            is Resource.Loading -> loadingState(true)
            is Resource.Success -> handleSuccess()
            null -> Unit
        }
    }

    private fun handleError(e: Exception) {
        val message = requireContext().exceptionConverter(e)
        toast(message)
        loadingState(false)
    }

    private fun handleSuccess() {
        loadingState(false)
        findNavController().popBackStack()
    }

    private fun imageObserver(uri: Uri?) {
        uri?.let {
            loadImage(it)
        }
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
        vm.deleteImage()
    }

    private fun validate(): Boolean = with(binding) {
        val isValidUsername = inputUsername.validate(
            rules = listOf(
                EmptyRule(),
                LengthRule(4, 16, R.string.invalid_username)
            )
        )

        val isValidAbout = inputAbout.validate(
            rules = listOf(
                MaxLengthRule(50)
            )
        )

        return isValidUsername && isValidAbout
    }

    private fun convert(): File {
        return UriToFile(requireContext()).convert(UserManager.user!!.username, vm.imageUri.value!!)
    }

    private fun <T> Flow<T>.createObserver(action: (T) -> Unit) {
        onEach(action).launchIn(viewLifecycleOwner.lifecycleScope)
    }

}
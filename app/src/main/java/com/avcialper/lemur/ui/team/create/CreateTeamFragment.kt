package com.avcialper.lemur.ui.team.create

import android.net.Uri
import android.view.View
import androidx.fragment.app.viewModels
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentCreateTeamBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.MaxLengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.ImageUpdateSheet
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class CreateTeamFragment :
    BaseFragment<FragmentCreateTeamBinding>(FragmentCreateTeamBinding::inflate) {

    private val vm: CreateTeamViewModel by viewModels()

    private lateinit var imagePicker: ImagePicker
    private var imageUri: Uri? = null

    override fun FragmentCreateTeamBinding.initialize() {
        imagePicker = ImagePicker(this@CreateTeamFragment) { uri ->
            imageUri = uri
            teamImage.apply {
                setImageURI(imageUri)
                visibility = View.VISIBLE
            }
            tvAddImage.visibility = View.GONE
        }

        setListeners()
        observe()
    }

    private fun setListeners() = with(binding) {
        buttonCreateTeam.setOnClickListener {
            val isValid = validate()
            if (isValid) {
                var file: File? = null
                if (imageUri != null)
                    file = imageUri!!.convertFile()

                vm.createTeam(inputTeamName.value, inputTeamDescription.value, file)
            }
        }
        tvAddImage.setOnClickListener {
            imagePicker.pickImage()
        }
        teamImage.setOnClickListener {
            ImageUpdateSheet({
                imageUri = null
                teamImage.visibility = View.GONE
                tvAddImage.visibility = View.VISIBLE
            }, { imagePicker.pickImage() }).show(childFragmentManager, "image_update_sheet")
        }
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
    }

    private fun handleSuccess() {
        goBack()
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        inputTeamName.setLoadingState(isLoading)
        inputTeamDescription.setLoadingState(isLoading)
        buttonCreateTeam.updateLoadingState(isLoading)
        tvAddImage.updateLoadingState(isLoading)
        teamImage.updateLoadingState(isLoading)
    }

    private fun validate(): Boolean {
        val maxTeamNameLength = getInt(R.integer.max_team_name_length)
        val isValidTeamName = binding.inputTeamName.validate(
            rules = listOf(
                EmptyRule(),
                MaxLengthRule(maxTeamNameLength)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.team_name,
                    maxTeamNameLength
                )
            }
        )

        val maxTeamDescriptionLength = getInt(R.integer.max_team_description_length)
        val isValidDescription = binding.inputTeamDescription.validate(
            rules = listOf(
                EmptyRule(),
                MaxLengthRule(maxTeamDescriptionLength),
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.description,
                    maxTeamDescriptionLength
                )
            }
        )

        return isValidTeamName && isValidDescription
    }
}
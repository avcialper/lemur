package com.avcialper.lemur.ui.team.update

import android.net.Uri
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.load
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.Team
import com.avcialper.lemur.databinding.FragmentUpdateTeamBinding
import com.avcialper.lemur.helper.ImagePicker
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.MaxLengthRule
import com.avcialper.lemur.ui.BaseFragment
import com.avcialper.lemur.ui.component.ImageUpdateSheet
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class UpdateTeamFragment :
    BaseFragment<FragmentUpdateTeamBinding>(FragmentUpdateTeamBinding::inflate) {

    private val vm: UpdateTeamViewModel by viewModels()
    private val args: UpdateTeamFragmentArgs by navArgs()

    private lateinit var imagePicker: ImagePicker
    private var imageUri: Uri? = null
    private var isImageDeleted = false

    override fun FragmentUpdateTeamBinding.initialize() {
        imagePicker = ImagePicker(this@UpdateTeamFragment) { uri ->
            imageUri = uri
            teamImage.load(imageUri)
        }

        vm.getTeamDetails(args.teamId)
        observe()
        setListeners()
    }

    private fun setListeners() = with(binding) {
        teamImage.setOnClickListener {
            if (vm.state.value.data?.imageUrl != null || imageUri != null)
                ImageUpdateSheet(::onImageDeleted, imagePicker::pickImage).show(
                    childFragmentManager,
                    "image_selector"
                )
            else
                imagePicker.pickImage()
        }

        buttonUpdateTeam.setOnClickListener {
            val isValid = validate()
            if (isValid) {
                var file: File? = null
                if (imageUri != null)
                    file = imageUri!!.convertFile()

                val imageUrl = if (isImageDeleted) null else vm.state.value.data!!.imageUrl

                vm.updateTeam(
                    args.teamId,
                    inputTeamName.value,
                    inputTeamDescription.value,
                    imageUrl,
                    file
                )
            }
        }
    }

    private fun onImageDeleted() {
        isImageDeleted = true
        imageUri = null
        binding.teamImage.load(R.drawable.logo)
    }

    private fun observe() {
        vm.state.createResourceObserver(::handleSuccess, ::handleLoading)
        vm.updateState.createResourceObserver(::handleUpdateSuccess, ::handleLoading)
    }

    private fun handleSuccess(team: Team?) = with(binding) {
        team?.let {
            inputTeamName.value = it.name
            inputTeamDescription.value = it.description

            it.imageUrl?.let { image ->
                teamImage.load(image)
            }
        }
    }

    private fun handleUpdateSuccess() {
        goBack()
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        val visibility = if (isLoading) View.GONE else View.VISIBLE
        inputTeamName.visibility = visibility
        inputTeamDescription.visibility = visibility
        buttonUpdateTeam.visibility = visibility
        teamImage.visibility = visibility

        progress.visibility = if (isLoading) View.VISIBLE else View.GONE
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
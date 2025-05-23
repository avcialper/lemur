package com.avcialper.lemur.ui.team.component.join

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.avcialper.lemur.R
import com.avcialper.lemur.databinding.FragmentJoinTeamBinding
import com.avcialper.lemur.helper.validator.EmptyRule
import com.avcialper.lemur.helper.validator.RequireLengthRule
import com.avcialper.lemur.util.constant.Resource
import com.avcialper.lemur.util.extension.formatInvalidLengthError
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class JoinTeamBottomSheet(
    private val onSuccess: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentJoinTeamBinding? = null
    private val binding get() = _binding!!

    private val vm: JoinTeamViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinTeamBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {

            btnJoin.setOnClickListener {
                val isValid = validate()
                if (isValid)
                    vm.joinTeam(inviteCode.value)
            }

            vm.state.onEach(::handleResource).launchIn(viewLifecycleOwner.lifecycleScope)

        }
    }

    private fun handleResource(resource: Resource<Boolean>?) {
        when (resource) {
            is Resource.Success -> {
                handleLoading(false)
                if (resource.data == true) {
                    dismiss()
                    onSuccess.invoke()
                } else {
                    val errorMessage = requireContext().getString(R.string.join_error)
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            }

            is Resource.Error -> {
                handleLoading(false)
                val errorMessage = requireContext().getString(R.string.join_error)
                Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
            }

            is Resource.Loading -> handleLoading(true)
            else -> {}
        }
    }

    private fun handleLoading(isLoading: Boolean) = with(binding) {
        inviteCode.setLoadingState(isLoading)
        btnJoin.apply {
            alpha = if (isLoading) 0.5f else 1f
            isEnabled = !isLoading
        }
    }

    private fun validate(): Boolean {
        return binding.inviteCode.validate(
            rules = listOf(
                EmptyRule(),
                RequireLengthRule(8)
            ),
            formatErrorMessage = { errorMessage ->
                errorMessage.formatInvalidLengthError(
                    requireContext(),
                    R.string.invite_code,
                    8
                )
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
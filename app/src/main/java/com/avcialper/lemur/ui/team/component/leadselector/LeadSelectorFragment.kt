package com.avcialper.lemur.ui.team.component.leadselector

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.avcialper.lemur.R
import com.avcialper.lemur.data.model.local.SelectableMemberCard
import com.avcialper.lemur.databinding.FragmentLeadSelectorBinding
import com.avcialper.lemur.ui.component.AlertFragment
import com.avcialper.lemur.ui.team.component.leadselector.adapter.LeadSelectorAdapter
import com.avcialper.lemur.util.constant.Resource
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.divider.MaterialDividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class LeadSelectorFragment(
    private val teamId: String,
    private val handleDismiss: () -> Unit,
    private val handleLeave: () -> Unit,
    private val handleDelete: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentLeadSelectorBinding? = null
    private val binding get() = _binding!!

    private val vm: LeadSelectorViewModel by viewModels()
    private var selectedLead: SelectableMemberCard? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        vm.getMembers(teamId)
        _binding = FragmentLeadSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()

        vm.state.onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    dismiss()
                }

                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.buttonSelectNewOwner.isEnabled = false
                }

                is Resource.Success -> {
                    binding.progress.visibility = View.GONE

                    val members = resource.data ?: emptyList()

                    if (members.size == 1) {
                        dismiss()
                        AlertFragment(
                            R.string.leave_team_and_delete_question,
                            onPositiveClick = handleDelete
                        ).show(
                            parentFragmentManager,
                            "alert"
                        )
                    } else {
                        createRecyclerView(members)
                    }
                }
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        vm.changeState.onEach { resource ->
            when (resource) {
                is Resource.Error -> {
                    dismiss()
                }

                is Resource.Loading -> {
                    binding.progress.visibility = View.VISIBLE
                    binding.buttonSelectNewOwner.visibility = View.GONE
                    binding.rvMembers.visibility = View.GONE
                }

                is Resource.Success -> {
                    dismiss()
                    if (resource.data == true)
                        handleLeave()
                }

                else -> {}
            }
        }.launchIn(viewLifecycleOwner.lifecycleScope)

        binding.buttonSelectNewOwner.setOnClickListener {
            val label =
                requireContext().getString(R.string.change_team_owner_and_leave_team_question)
                    .replace("{0}", selectedLead!!.name)

            AlertFragment(stringLabel = label) {
                vm.changeTeamOwner(teamId, selectedLead!!.id)
            }.show(childFragmentManager, "alert")

        }
    }

    private fun createRecyclerView(data: List<SelectableMemberCard>) {
        val adapter = LeadSelectorAdapter(data) {
            if (!binding.buttonSelectNewOwner.isEnabled)
                binding.buttonSelectNewOwner.isEnabled = true
            selectedLead = it
        }
        val layoutManager = LinearLayoutManager(requireContext())
        val divider =
            MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            ).apply {
                isLastItemDecorated = false
            }

        binding.rvMembers.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
            addItemDecoration(divider)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        handleDismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
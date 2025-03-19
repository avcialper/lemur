package com.avcialper.lemur.ui.component

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.avcialper.lemur.databinding.FragmentProfilePictureUpdateBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProfilePictureUpdateSheet(
    private val onDelete: () -> Unit,
    private val onUpdate: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentProfilePictureUpdateBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfilePictureUpdateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        componentUpdate.setOnClickListener {
            onUpdate.invoke()
            dismiss()
        }
        componentDelete.setOnClickListener {
            onDelete.invoke()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
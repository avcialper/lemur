package com.avcialper.lemur.ui.component.imageselector

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.avcialper.lemur.databinding.FragmentImageSelectorBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class PartialImageViewer(
    private val uris: MutableList<Uri>,
    private val onSelected: (Uri) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentImageSelectorBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageSelectorBinding.inflate(layoutInflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = PartialImageViewerAdapter(uris) { uri ->
            onSelected.invoke(uri)
            dismiss()
        }

        val layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)

        binding.rvImage.apply {
            this.adapter = adapter
            this.layoutManager = layoutManager
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
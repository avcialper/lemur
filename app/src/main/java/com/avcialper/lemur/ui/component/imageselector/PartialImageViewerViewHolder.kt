package com.avcialper.lemur.ui.component.imageselector

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.avcialper.lemur.databinding.ItemImageSelectorBinding

class PartialImageViewerViewHolder(
    private val binding: ItemImageSelectorBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(uri: Uri, onSelected: (Uri) -> Unit) {
        binding.image.apply {
            load(uri) {
                crossfade(true)
            }
            setOnClickListener {
                onSelected(uri)
            }
        }
    }

}
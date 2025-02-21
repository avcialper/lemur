package com.avcialper.lemur.ui.component.imageselector

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.databinding.ItemImageSelectorBinding

class PartialImageViewerAdapter(
    private val data: List<Uri>,
    private val onSelected: (Uri) -> Unit
) : RecyclerView.Adapter<PartialImageViewerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartialImageViewerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemImageSelectorBinding.inflate(layoutInflater, parent, false)
        return PartialImageViewerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PartialImageViewerViewHolder, position: Int) {
        val uri = data[position]
        holder.bind(uri, onSelected)
    }

    override fun getItemCount(): Int = data.size
}
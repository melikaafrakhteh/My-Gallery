package com.afrakhteh.mygallery.view.main.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.afrakhteh.mygallery.databinding.RecyclerItemRowBinding
import com.afrakhteh.mygallery.model.entity.ImageEntity

class ImageAdapter(
    private val remove: (Int) -> Unit
) : ListAdapter<ImageEntity, ImageViewHolder>(ImageDiffCallBack()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = RecyclerItemRowBinding.inflate(inflater)
        return ImageViewHolder(binding, remove, parent.context)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind()
    }

    override fun onViewAttachedToWindow(holder: ImageViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.loadImage(getItem(holder.adapterPosition).path)
    }

    override fun onViewDetachedFromWindow(holder: ImageViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.stopLoadingImage()
    }
}
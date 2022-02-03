package com.afrakhteh.mygallery.view.main.adapter

import androidx.recyclerview.widget.DiffUtil
import com.afrakhteh.mygallery.model.entity.ImageEntity

class ImageDiffCallBack : DiffUtil.ItemCallback<ImageEntity>(){
    override fun areItemsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: ImageEntity, newItem: ImageEntity): Boolean {
        return oldItem == newItem
    }
}
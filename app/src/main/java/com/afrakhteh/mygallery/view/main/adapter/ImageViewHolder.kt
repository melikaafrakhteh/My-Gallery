package com.afrakhteh.mygallery.view.main.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.afrakhteh.mygallery.constant.Numerals
import com.afrakhteh.mygallery.databinding.RecyclerItemRowBinding
import com.afrakhteh.mygallery.model.entity.ImageEntity
import com.afrakhteh.mygallery.util.resize
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream


class ImageViewHolder(
    private val binding: RecyclerItemRowBinding,
    private val remove: (ImageEntity) -> Unit,
    private val context: Context
) : RecyclerView.ViewHolder(binding.root) {

    private var getImageJob: Job? = null

    fun bind(data: ImageEntity) {
        binding.recyclerItemImageIv.setImageDrawable(null)
        binding.recyclerItemImageDeleteIv.setOnClickListener {
            remove.invoke(data)
        }
    }

    fun loadImage(uri: Uri) {
        getImageJob = CoroutineScope(Dispatchers.Main).launch {
            try {
                Glide.with(context).asBitmap().load(uri)
                    .into(
                        object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                val out = ByteArrayOutputStream()
                               resource.compress(Bitmap.CompressFormat.JPEG, 90, out)
                                binding.recyclerItemImageIv.setImageBitmap(
                                    resource.resize(Numerals.IMAGE_MAX_SIZE)
                                )
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {}
                        }
                    )

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun stopLoadingImage() {
        getImageJob?.cancel()
    }
}
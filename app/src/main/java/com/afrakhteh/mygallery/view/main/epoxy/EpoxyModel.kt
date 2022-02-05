package com.afrakhteh.mygallery.view.main.epoxy

import android.net.Uri
import android.widget.ImageView
import com.afrakhteh.mygallery.R
import com.afrakhteh.mygallery.R.layout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions


@EpoxyModelClass(layout = layout.item_row)
abstract class EpoxyModel : EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute
    lateinit var imageUrl: Uri

    @EpoxyAttribute
    lateinit var deleteListener: () -> Unit

    override fun bind(holder: Holder) {
        Glide.with(holder.imageView).load(imageUrl).encodeQuality(90)
            .apply(RequestOptions.overrideOf(1000, 1000))
            .into(holder.imageView)
        holder.imageDelete.setOnClickListener { deleteListener.invoke() }
    }

}

class Holder : KotlinEpoxyHolder() {
    val imageView by bind<ImageView>(R.id.recyclerItemImageIv)
    val imageDelete by bind<ImageView>(R.id.recyclerItemImageDeleteIv)
}
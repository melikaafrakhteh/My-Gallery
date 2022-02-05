package com.afrakhteh.mygallery.view.main.epoxy

import android.net.Uri
import android.widget.ImageView
import com.afrakhteh.mygallery.R
import com.afrakhteh.mygallery.R.layout
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder


@EpoxyModelClass(layout = layout.item_row)
abstract class EpoxyModel : EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute
    lateinit var imageUrl: Uri
    @EpoxyAttribute
    lateinit var deleteListener: () -> Unit

    override fun bind(holder: Holder) {
        holder.imageView.setImageURI(imageUrl)
        holder.imageDelete.setOnClickListener{ deleteListener.invoke()}
    }

}

class Holder : KotlinEpoxyHolder() {
    val imageView by bind<ImageView>(R.id.recyclerItemImageIv)
    val imageDelete by bind<ImageView>(R.id.recyclerItemImageDeleteIv)
}
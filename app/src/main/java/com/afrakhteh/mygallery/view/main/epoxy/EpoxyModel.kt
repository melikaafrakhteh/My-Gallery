package com.afrakhteh.mygallery.view.main.epoxy

import android.net.Uri
import android.widget.ImageView
import com.afrakhteh.mygallery.R
import com.afrakhteh.mygallery.model.entity.ImageEntity
import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder

@EpoxyModelClass(layout = R.layout.recycler_item_row)
abstract class EpoxyModel : EpoxyModelWithHolder<Holder>() {

    @EpoxyAttribute
    lateinit var imageUrl: Uri
    @EpoxyAttribute
    lateinit var deleteListener: (ImageEntity) -> Unit

    override fun bind(holder: Holder) {
        holder.imageView.setImageURI(imageUrl)
        holder.imageDelete.setOnClickListener{ deleteListener.invoke(ImageEntity())}
    }

}

class Holder : KotlinEpoxyHolder() {
    val imageView by bind<ImageView>(R.id.recyclerItemImageIv)
    val imageDelete by bind<ImageView>(R.id.recyclerItemImageDeleteIv)
}
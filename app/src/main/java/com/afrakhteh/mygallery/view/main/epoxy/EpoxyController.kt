package com.afrakhteh.mygallery.view.main.epoxy

import com.afrakhteh.mygallery.model.entity.ImageEntity
import com.airbnb.epoxy.TypedEpoxyController

class EpoxyController(
    private val delete: (ImageEntity) -> Unit
) : TypedEpoxyController<List<ImageEntity>>() {

    override fun buildModels(data: List<ImageEntity>?) {
        data?.forEach { item ->
            epoxy {
                id(hashCode())
                imageUrl(item.path)
                deleteListener { this@EpoxyController.delete.invoke(item) }
            }

        }
    }
}
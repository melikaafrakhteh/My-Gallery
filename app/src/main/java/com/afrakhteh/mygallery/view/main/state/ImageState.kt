package com.afrakhteh.mygallery.view.main.state

import com.afrakhteh.mygallery.model.entity.ImageEntity
import com.afrakhteh.mygallery.util.SingleEvent

data class ImageState(
    private val list: List<ImageEntity> = emptyList(),
    private val errorMessage: SingleEvent<String> ?= null
)

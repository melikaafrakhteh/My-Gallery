package com.afrakhteh.mygallery.util

import android.graphics.Bitmap

fun Bitmap.resize(maxSize: Int): Bitmap {
    var width = this.width
    var height = this.height
    val ratio = width.toFloat() / height.toFloat()
    if (ratio > 1) {
        width = maxSize
        height = (width / ratio).toInt()
    } else {
        height = maxSize
        width = (height * ratio).toInt()
    }
    return Bitmap.createScaledBitmap(this, width, height, true)
}
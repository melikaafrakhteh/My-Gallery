package com.afrakhteh.mygallery.util

class SingleEvent<T> (private val value: T ) {
    private var isHandled: Boolean = false

    fun ifNotHandled(block: (T) -> Unit) {
        if (isHandled.not()) {
            isHandled = true
            block.invoke(value)
        }
    }
}
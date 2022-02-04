package com.afrakhteh.mygallery.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afrakhteh.mygallery.model.entity.ImageEntity
import com.afrakhteh.mygallery.view.main.state.ImageState

class MainViewModel : ViewModel() {

    private val pState = MutableLiveData<ImageState>()
    val state: LiveData<ImageState> get() = pState

    private var imageList: ArrayList<ImageEntity> = arrayListOf()

    fun fetchAllImages() {
        pState.value = ImageState(list = imageList)
    }

    fun addNewItemToList(uri: Uri?) {
        imageList.add(ImageEntity(requireNotNull(uri)))
    }

}
package com.afrakhteh.mygallery.viewModel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.afrakhteh.mygallery.model.entity.ImageEntity
import com.afrakhteh.mygallery.view.main.state.ImageState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val pState = MutableLiveData<ImageState>()
    val state: LiveData<ImageState> get() = pState

    private var deleteJob: Job? = null
    private var itemJob: Job? = null

    private var imageList: MutableList<ImageEntity> = mutableListOf()

    fun fetchAllImages() {
        itemJob = CoroutineScope(Dispatchers.IO).launch {
             pState.postValue(ImageState(list = imageList))

        }
    }

    fun addNewItemToList(uri: Uri?) {
        imageList.add(ImageEntity(requireNotNull(uri)))
    }

    fun deleteItemFromList(data: ImageEntity) {
        deleteJob = CoroutineScope(Dispatchers.Main).launch {
            imageList.remove(data)
           pState.postValue(pState.value?.copy(list = imageList as List<ImageEntity>))
        }
    }

    override fun onCleared() {
        deleteJob?.cancel()
        itemJob?.cancel()
        super.onCleared()
    }
}
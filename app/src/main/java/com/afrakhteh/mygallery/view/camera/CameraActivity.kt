package com.afrakhteh.mygallery.view.camera

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.afrakhteh.mygallery.databinding.ActivityCameraBinding
import com.google.common.util.concurrent.ListenableFuture

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCamera()
        binding.cameraCaptureBtn.setOnClickListener(::takePhoto)
    }

    private fun takePhoto(view: View?) {

    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            bindToLifeCycle(cameraProviderFuture)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindToLifeCycle(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, addPreview()
            )

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addPreview(): Preview {
        return Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
    }
}
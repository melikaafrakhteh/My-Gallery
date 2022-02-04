package com.afrakhteh.mygallery.view.camera

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Size
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.afrakhteh.mygallery.constant.Strings
import com.afrakhteh.mygallery.databinding.ActivityCameraBinding
import com.afrakhteh.mygallery.view.main.MainActivity
import com.google.common.util.concurrent.ListenableFuture
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCamera()
        binding.cameraCaptureBtn.setOnClickListener(::takePhoto)
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun createPhotoUriFormat(): ContentValues {
        val name = SimpleDateFormat(Strings.FILE_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }
        return contentValues
    }

    private fun createOutPut(): ImageCapture.OutputFileOptions {
        return ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                createPhotoUriFormat()
            )
            .build()
    }

    private fun takePhoto(view: View?) {
        val imageCapture = imageCapture ?: return
        val outputOptions = createOutPut()
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(
                        this@CameraActivity,
                        "Photo capture failed: ${exc.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    sendUri(requireNotNull(output.savedUri))
                }
            }
        )
    }

    private fun sendUri(uri: Uri) {
        Intent(this, MainActivity::class.java).apply {
            putExtra(Strings.URI_KEY, uri.toString())
            setResult(Activity.RESULT_OK, this)
        }
        finish()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            bindToLifeCycle(cameraProviderFuture)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindToLifeCycle(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        imageCapture = ImageCapture.Builder()
            .setTargetResolution(Size(1000, 1000))
            .setJpegQuality(90)
            .build()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        try {
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this, cameraSelector, addPreview(), imageCapture
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

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}
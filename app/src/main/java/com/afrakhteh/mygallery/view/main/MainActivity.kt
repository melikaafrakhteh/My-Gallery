package com.afrakhteh.mygallery.view.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afrakhteh.mygallery.constant.Numerals
import com.afrakhteh.mygallery.databinding.ActivityMainBinding
import com.afrakhteh.mygallery.model.entity.ImageEntity
import com.afrakhteh.mygallery.view.camera.CameraActivity
import com.afrakhteh.mygallery.view.main.adapter.ImageAdapter
import com.afrakhteh.mygallery.view.main.adapter.SpaceItemDecoration
import com.afrakhteh.mygallery.view.main.custom.ChooseDialog


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var imageAdapter: ImageAdapter
    private var imageList: ArrayList<ImageEntity> = arrayListOf()

    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageList.add(ImageEntity(requireNotNull(uri)))
        imageAdapter.submitList(imageList)
        if (imageList.size != 0) {
            binding.mainEmptyListTextTv.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE

            val lparams = binding.mainAddImageBtn.layoutParams as ConstraintLayout.LayoutParams
            lparams.verticalBias = 0.04f
            binding.mainAddImageBtn.layoutParams = lparams
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageAdapter = ImageAdapter(::deleteImageFromList)
        binding.mainAddImageBtn.setOnClickListener(::chooseImagesResource)

        binding.mainRecyclerView.visibility = View.INVISIBLE
        binding.mainRecyclerView.adapter = imageAdapter
        binding.mainRecyclerView.addItemDecoration(SpaceItemDecoration(40))

    }

    private fun deleteImageFromList(position: Int) {

    }

    private fun chooseImagesResource(view: View?) {
        ChooseDialog(::chooseGallery, ::chooseCamera).show(supportFragmentManager, "dialog")
    }

    private fun chooseCamera() {
        if (hasThisPermissionGranted(Manifest.permission.CAMERA)) {
            openCamera()
        } else {
            requestPermission(
                Manifest.permission.CAMERA,
                Numerals.REQUEST_CAMERA_CODE
            )
        }
    }

    private fun chooseGallery() {
        if (hasThisPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openGallery()
        } else {
            requestPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Numerals.REQUEST_READ_STORAGE_CODE
            )
        }
    }

    private fun hasThisPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }


    private fun requestPermission(permission: String, code: Int) {
        if (!hasThisPermissionGranted(permission)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                code
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                Numerals.REQUEST_READ_STORAGE_CODE -> openGallery()
                Numerals.REQUEST_CAMERA_CODE -> openCamera()
            }
        } else {
            //deny
        }
    }

    private fun openCamera() {
        startActivity(Intent(this, CameraActivity::class.java))
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }
}

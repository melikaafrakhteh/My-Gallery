package com.afrakhteh.mygallery.view.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.afrakhteh.mygallery.R
import com.afrakhteh.mygallery.constant.Numerals
import com.afrakhteh.mygallery.constant.Strings
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
        addDataToList(uri!!)
    }
    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(Strings.URI_KEY).let {
                    addDataToList(it?.toUri())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageAdapter = ImageAdapter(::deleteImageFromList)
        initialiseView()
    }

    private fun initialiseView() {
        binding.mainRecyclerView.apply {
            visibility = View.INVISIBLE
            adapter = imageAdapter
            addItemDecoration(SpaceItemDecoration(40))
        }
        binding.mainAddImageBtn.setOnClickListener(::chooseImagesResource)
    }

    private fun initialiseViewWithItemState() {
        binding.mainEmptyListTextTv.visibility = View.GONE
        binding.mainRecyclerView.visibility = View.VISIBLE

        val lparams = binding.mainAddImageBtn.layoutParams as ConstraintLayout.LayoutParams
        lparams.verticalBias = 0.04f
        binding.mainAddImageBtn.layoutParams = lparams
    }

    private fun deleteImageFromList(position: Int) {
        val currentList = imageAdapter.currentList.toMutableList()
        currentList.removeAt(position)
        imageAdapter.submitList(currentList)
        Toast.makeText(applicationContext, getString(R.string.delete_msg), Toast.LENGTH_SHORT)
            .show()
    }

    private fun addDataToList(uri: Uri?) {
        imageList.add(ImageEntity(requireNotNull(uri)))
        imageAdapter.submitList(imageList)
        if (imageList.size != 0) {
            initialiseViewWithItemState()
        }
    }

    private fun chooseImagesResource(view: View?) {
        ChooseDialog(::chooseGallery, ::chooseCamera).show(supportFragmentManager, "dialog")
    }

    private fun chooseCamera() {
        if (
            hasThisPermissionGranted(Manifest.permission.CAMERA) and
            hasThisPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        ) {
            openCamera()
        } else {
            requestPermission(
                Manifest.permission.CAMERA,
                Numerals.REQUEST_CAMERA_CODE
            )
            requestPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Numerals.REQUEST_WRITE_STORAGE_CODE
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
        intentLauncher.launch(Intent(this, CameraActivity::class.java))
    }

    private fun openGallery() {
        getContent.launch("image/*")
    }

}

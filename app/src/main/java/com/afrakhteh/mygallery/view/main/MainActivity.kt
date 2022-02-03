package com.afrakhteh.mygallery.view.main

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afrakhteh.mygallery.constant.Numerals
import com.afrakhteh.mygallery.databinding.ActivityMainBinding
import com.afrakhteh.mygallery.model.entity.ImageEntity
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
        if (imageList.size != 0){
            binding.mainEmptyListTextTv.visibility = View.GONE
            binding.mainRecyclerView.visibility = View.VISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageAdapter = ImageAdapter(::deleteImageFromList)
        binding.mainAddImageBtn.setOnClickListener(::chooseImagesResource)

        binding.mainRecyclerView.visibility = View.GONE
        binding.mainRecyclerView.adapter = imageAdapter
        binding.mainRecyclerView.addItemDecoration(SpaceItemDecoration(36))

    }

    private fun deleteImageFromList(position: Int) {

    }

    private fun chooseImagesResource(view: View?) {
        ChooseDialog(::chooseGallery, ::chooseCamera).show(supportFragmentManager, "dialog")
    }

    private fun chooseCamera() {
        Toast.makeText(this, "camera", Toast.LENGTH_SHORT).show()
    }

    private fun chooseGallery() {
        if (hasReadStoragePermissionGranted()) {
            openGallery()
        } else {
            requestStoragePermission()
        }
    }

    private fun hasReadStoragePermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestStoragePermission() {
        if (!hasReadStoragePermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Numerals.REQUEST_READ_STORAGE_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Numerals.REQUEST_READ_STORAGE_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery()
            } else {
                //deny
            }
        }
    }

    private fun openGallery() {
        Toast.makeText(this, "gallery", Toast.LENGTH_SHORT).show()
        getContent.launch("image/*")
    }
}

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
import androidx.activity.viewModels
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
import com.afrakhteh.mygallery.view.main.custom.DeleteDialog
import com.afrakhteh.mygallery.view.main.state.ImageState
import com.afrakhteh.mygallery.viewModel.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var imageAdapter: ImageAdapter

    private val viewModel: MainViewModel by viewModels()

    private val getContent = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.addNewItemToList(uri)
    }
    private val intentLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.getStringExtra(Strings.URI_KEY).let {
                    viewModel.addNewItemToList(it?.toUri())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageAdapter = ImageAdapter(::deleteImageFromList)
        initialiseEmptyStateView()
        binding.mainRecyclerView.apply {
            visibility = View.GONE
            adapter = imageAdapter
            addItemDecoration(SpaceItemDecoration(16))
        }
        viewModel.state.observe(this, ::renderList)
        binding.mainAddImageBtn.setOnClickListener(::chooseImagesResource)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchAllImages()
        val number = viewModel.state.value?.list?.size
        if (number == 0) {
            initialiseEmptyStateView()
        } else {
            initialiseWithItemStateView()
        }
    }

    private fun renderList(imageState: ImageState?) {
        imageState?.errorMessage?.ifNotHandled {
            Toast.makeText(applicationContext, getString(R.string.error_msg), Toast.LENGTH_SHORT)
                .show()
        }
        val number = imageState?.list?.size
        imageAdapter.submitList(imageState?.list)
        if (number == 0) {
            initialiseEmptyStateView()
        } else {
            initialiseWithItemStateView()
        }
    }

    private fun initialiseEmptyStateView() {
        binding.mainEmptyListTextTv.visibility = View.VISIBLE
        binding.mainRecyclerView.removeAllViews()
        val lparams = binding.mainAddImageBtn.layoutParams as ConstraintLayout.LayoutParams
        lparams.apply {
            topToBottom = R.id.mainEmptyListTextTv
            verticalBias = 0.139f
        }
        binding.mainAddImageBtn.layoutParams = lparams
    }

    private fun initialiseWithItemStateView() {
        binding.mainEmptyListTextTv.visibility = View.GONE
        binding.mainRecyclerView.visibility = View.VISIBLE

        val lparams = binding.mainAddImageBtn.layoutParams as ConstraintLayout.LayoutParams
        lparams.apply {
            topToBottom = R.id.mainRecyclerView
            verticalBias = 0.04f
        }
        binding.mainAddImageBtn.layoutParams = lparams
    }

    private fun deleteImageFromList(data: ImageEntity) {
        DeleteDialog {
            viewModel.deleteItemFromList(data)
            binding.mainRecyclerView.removeAllViewsInLayout()
            if (viewModel.state.value?.list!!.isEmpty())
                initialiseEmptyStateView()

            Toast.makeText(
                applicationContext,
                getString(R.string.delete_msg),
                Toast.LENGTH_SHORT
            ).show()
        }
            .show(supportFragmentManager, "delete")
    }

    private fun chooseImagesResource(view: View?) {
        ChooseDialog(::chooseGallery, ::chooseCamera).show(supportFragmentManager, "dialog")
    }

    private fun chooseCamera() {
        if (
            hasThisPermissionGranted(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        ) {
            openCamera()
        } else {
            requestPermission(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                Numerals.REQUEST_CAMERA_CODE and Numerals.REQUEST_WRITE_STORAGE_CODE
            )
        }
    }

    private fun chooseGallery() {
        if (hasThisPermissionGranted(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))) {
            openGallery()
        } else {
            requestPermission(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                Numerals.REQUEST_READ_STORAGE_CODE
            )
        }
    }

    private fun hasThisPermissionGranted(permissions: Array<String>): Boolean {
        return permissions.all {
            ContextCompat.checkSelfPermission(
                this,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermission(permissions: Array<String>, code: Int) {
        if (!hasThisPermissionGranted(permissions)) {
            ActivityCompat.requestPermissions(
                this,
                permissions,
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
        if (grantResults.isNotEmpty()
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            when (requestCode) {
                Numerals.REQUEST_READ_STORAGE_CODE -> openGallery()
                Numerals.REQUEST_CAMERA_CODE and Numerals.REQUEST_WRITE_STORAGE_CODE -> {
                    if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        openCamera()
                    }
                }
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

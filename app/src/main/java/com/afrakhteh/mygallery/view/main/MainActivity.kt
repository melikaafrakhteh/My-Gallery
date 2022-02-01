package com.afrakhteh.mygallery.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.afrakhteh.mygallery.R
import com.afrakhteh.mygallery.databinding.ActivityMainBinding
import com.afrakhteh.mygallery.view.main.custom.ChooseDialog

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainAddImageBtn.setOnClickListener(::chooseImagesResource)
    }

    private fun chooseImagesResource(view: View?) {
        ChooseDialog(::chooseGallery, ::chooseCamera).show(supportFragmentManager, "dialog")
    }

    private fun chooseCamera() {
        Toast.makeText(this, "camera", Toast.LENGTH_SHORT).show()
    }

    private fun chooseGallery() {
        Toast.makeText(this, "gallery", Toast.LENGTH_SHORT).show()
    }
}
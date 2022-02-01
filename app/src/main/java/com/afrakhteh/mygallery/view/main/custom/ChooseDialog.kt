package com.afrakhteh.mygallery.view.main.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.afrakhteh.mygallery.R
import com.afrakhteh.mygallery.databinding.DialogChooseBinding

class ChooseDialog(
    private val chooseGallery: () -> Unit,
    private val chooseCamera: () -> Unit
) : DialogFragment() {

    private var dialogBinding: DialogChooseBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        val binding = DialogChooseBinding.inflate(inflater, container, false)
        dialogBinding = binding
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog?.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(requireNotNull(dialogBinding)) {
            dialogChooseGalleryBtn.setOnClickListener {
                chooseGallery.invoke()
                dialog?.dismiss()
            }
            dialogChooseCameraBtn.setOnClickListener {
                chooseCamera.invoke()
                dialog?.dismiss()
            }
        }
    }

    override fun onDestroy() {
        dialogBinding = null
        super.onDestroy()
    }
}
package com.example.agrotracker.ui.add_seal

import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.agrotracker.databinding.FragmentAddSealBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddSealFragment : Fragment() {
    private var _binding: FragmentAddSealBinding? = null
    private val binding get() = _binding!!

    private val takePhotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { captured ->
            if (captured) {
                proceedPhoto()
            } else {
                askToRetakePhoto()
            }
        }

    private fun takePhoto() {
        val file = createImageFile()
        val uri = FileProvider.getUriForFile(
            requireContext(),
            "com.example.android.fileprovider",
            file
        )
        takePhotoLauncher.launch(uri)
    }

    private var currentImagePath: String? = null

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentImagePath = absolutePath
        }
    }


    private fun proceedPhoto() {
        binding.path.text = currentImagePath
    }

    private fun askToRetakePhoto() {

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSealBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    private fun initViews() {
        binding.submit.setOnClickListener {
            setFragmentResult(
                "add_seal",
                bundleOf(
                    "sealNumber" to binding.sealIdInput.text.toString(),
                    "imageUri" to null
                )
            )
            findNavController().popBackStack()
        }

        binding.takePhoto.setOnClickListener {
            takePhoto()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
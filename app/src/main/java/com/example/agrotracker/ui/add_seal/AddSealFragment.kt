package com.example.agrotracker.ui.add_seal

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrotracker.R
import com.example.agrotracker.databinding.FragmentAddSealBinding
import com.example.agrotracker.helpers.PhotoTaker
import com.example.agrotracker.helpers.SealNumberRecognizer
import com.example.agrotracker.utils.ResultKeys
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddSealFragment : Fragment(R.layout.fragment_add_seal) {

    private val binding: FragmentAddSealBinding by viewBinding()

    @Inject
    lateinit var photoTaker: PhotoTaker

    @Inject
    lateinit var sealNumberRecognizer: SealNumberRecognizer

    private val photoErrorHandler = CoroutineExceptionHandler { _, throwable ->
        logError(throwable)
        askToRetakePhoto()
    }

    private var photoUri: Uri? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.takePhoto.setOnClickListener {
            takePhoto()
        }

        binding.submit.setOnClickListener {
            setResult()
            findNavController().popBackStack()
        }
    }

    private fun takePhoto() {
        lifecycleScope.launch(photoErrorHandler) {
            photoTaker.takePhoto()?.let { uri ->
                proceedPhoto(uri)
            }
        }
    }

    private suspend fun proceedPhoto(uri: Uri) {
        val text = sealNumberRecognizer.recognize(uri)
        binding.sealIdInput.setText(text)
        photoUri = uri
    }

    private fun askToRetakePhoto() {

    }


    private fun setResult() {
        setFragmentResult(
            ResultKeys.CODE_ADD_SEAL,
            bundleOf(
                ResultKeys.SEAL_NUMBER to binding.sealIdInput.text.toString(),
                ResultKeys.SEAL_URI to photoUri
            )
        )
    }

    private fun logError(
        throwable: Throwable,
        message: String = "Error: ${throwable.localizedMessage}"
    ) {
        Log.e("AddSealFragment", message, throwable)
    }
}
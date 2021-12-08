package com.example.agrotracker.ui.add_seal

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrotracker.R
import com.example.agrotracker.databinding.FragmentAddSealBinding
import com.example.agrotracker.helpers.PhotoTaker
import com.example.agrotracker.utils.Regexes
import com.example.agrotracker.utils.ResultKeys
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddSealFragment : Fragment(R.layout.fragment_add_seal) {

    private val binding: FragmentAddSealBinding by viewBinding()

    private val viewModel: AddSealViewModel by viewModels()

    @Inject
    lateinit var photoTaker: PhotoTaker

    private var photoUri: Uri? = null

    private var errorSnackBar: Snackbar? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        setupStateObservers()
    }

    private fun initViews() {
        activity?.actionBar?.title = "Add Seal"

        binding.capturePhoto.setOnClickListener {
            errorSnackBar?.dismiss()
            capturePhoto()
        }

        binding.pickFromGallery.setOnClickListener {
            errorSnackBar?.dismiss()
            pickPhotoFromGallery()
        }

        binding.submit.setOnClickListener {
            setResult()
            findNavController().popBackStack()
        }

        binding.sealNumberInput.doOnTextChanged { text, _, _, _ ->
            val matches = text?.matches(Regexes.SEAL_NUMBER_REGEX) == true
            binding.submit.isEnabled = matches
            binding.textField.error = if (matches) "" else "Invalid seal number"
        }
    }

    private fun capturePhoto() {
        lifecycleScope.launch {
            photoTaker.capturePhoto()?.let { uri ->
                viewModel.recognizeSealNumber(uri)
            }
        }
    }

    private fun pickPhotoFromGallery() {
        lifecycleScope.launch {
            photoTaker.pickFromGallery()?.let { uri ->
                viewModel.recognizeSealNumber(uri)
            }
        }
    }

    private fun setupStateObservers() {
        viewModel.sealNumberRecognitionStateLiveData.observe(this) { state ->
            when (state) {
                is SealRecognitionState.Success -> {
                    showContent()
                    binding.sealNumberInput.setText(state.result.number)
                    photoUri = state.result.uri
                }
                is SealRecognitionState.Loading -> {
                    showProgress()
                }
                is SealRecognitionState.Error -> {
                    showContent()
                    askToRetakePhoto()
                }
            }
        }
    }

    private fun setResult() {
        setFragmentResult(
            ResultKeys.CODE_ADD_SEAL,
            bundleOf(
                ResultKeys.SEAL_NUMBER to binding.sealNumberInput.text.toString(),
                ResultKeys.SEAL_PHOTO_NAME to photoUri?.lastPathSegment
            )
        )
    }

    private fun askToRetakePhoto() {
        errorSnackBar = Snackbar.make(
            binding.root,
            "Номер не розпізнано, спробуйте ще",
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction("Take photo") {
                capturePhoto()
            }
        errorSnackBar?.show()
    }


    private fun showContent() {
        binding.root.children.forEach { it.isVisible = true }
        binding.progressBar.isVisible = false
    }

    private fun showProgress() {
        binding.root.children.forEach { it.isVisible = false }
        binding.progressBar.isVisible = true
    }
}
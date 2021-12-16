package com.example.agrotracker.ui.add_seal

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.agrotracker.R
import com.example.agrotracker.databinding.FragmentAddSealBinding
import com.example.agrotracker.photo.PhotoTaker
import com.example.agrotracker.utils.ResultKeys
import com.example.data.utils.Regexes
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddSealFragment : Fragment(R.layout.fragment_add_seal) {

    private val binding by viewBinding(FragmentAddSealBinding::bind)

    private val viewModel: AddSealViewModel by viewModels()

    @Inject
    lateinit var photoTaker: PhotoTaker

    private var photoUri: Uri? = null


    private var errorSnackBar: Snackbar? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        observeState()
    }

    private fun initViews() {
        binding.scanSeal.setOnClickListener {
            errorSnackBar?.dismiss()
            scanSeal()
        }

        binding.submit.setOnClickListener {
            setResult()
            findNavController().popBackStack()
        }

        binding.sealNumberInput.doOnTextChanged { text, _, _, _ ->
            val matches = text?.matches(Regexes.SEAL_NUMBER_REGEX) == true
            binding.submit.isEnabled = matches
            binding.textField.error = if (matches) "" else getString(R.string.invalid_seal_number)
        }
    }

    private fun scanSeal() {
        lifecycleScope.launch {
            photoTaker.takePhoto()?.let { uri ->
                viewModel.recognizeSealNumber(uri)
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


    private fun observeState() {
        viewModel.recognitionStateLiveData.observe(this) { state ->
            when (state) {
                is RecognitionState.Success -> {
                    showContent()
                    binding.sealNumberInput.setText(state.result.number)
                    photoUri = state.result.uri
                }
                is RecognitionState.Loading -> {
                    showProgress()
                }
                is RecognitionState.Error -> {
                    showContent()
                    askToRetakePhoto()
                }
            }
        }
    }

    private fun showContent() {
        binding.root.children.forEach { it.isVisible = true }
        binding.progressBar.isVisible = false
    }

    private fun showProgress() {
        binding.root.children.forEach { it.isVisible = false }
        binding.progressBar.isVisible = true
    }

    private fun askToRetakePhoto() {
        errorSnackBar = Snackbar.make(
            binding.root,
            R.string.seal_number_not_recognized,
            Snackbar.LENGTH_INDEFINITE
        ).setAction(R.string.take_photo) {
            scanSeal()
        }
        errorSnackBar?.show()
    }
}
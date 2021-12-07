package com.example.agrotracker.ui.add_seal

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.agrotracker.R
import com.example.agrotracker.databinding.FragmentAddSealBinding
import com.example.agrotracker.helpers.PhotoTaker
import com.example.agrotracker.utils.ResultKeys
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViews()
        setupStateObservers()
    }

    private fun initViews() {
        binding.takePhoto.setOnClickListener {
            takePhoto()
        }

        binding.submit.setOnClickListener {
            setResult()
            findNavController().popBackStack()
        }
    }

    private fun takePhoto() {
        lifecycleScope.launch {
            photoTaker.takePhoto()?.let { uri ->
                viewModel.proceedPhoto(uri)
            }
        }
    }

    private fun setupStateObservers() {
        viewModel.sealNumberRecognitionStateLiveData.observe(this) { state ->
            when (state) {
                is AddSealState.Success -> {
                    binding.sealNumberInput.setText(state.result.number)
                    photoUri = state.result.uri
                }
                is AddSealState.Loading -> {

                }
                is AddSealState.Error -> {
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

    }
}
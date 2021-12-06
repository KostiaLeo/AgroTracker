package com.example.agrotracker.ui.add_seal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.example.agrotracker.databinding.FragmentAddSealBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddSealFragment : Fragment() {
    private var _binding: FragmentAddSealBinding? = null
    private val binding get() = _binding!!

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.agrotracker.ui.transport_details

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agrotracker.R
import com.example.agrotracker.databinding.FragmentTransportDetailsBinding
import com.example.agrotracker.ui.transport_details.seals_adapter.SealsAdapter
import com.example.agrotracker.utils.ResultKeys
import com.example.data.models.Transport
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransportDetailsFragment : Fragment(R.layout.fragment_transport_details) {

    private val binding: FragmentTransportDetailsBinding by viewBinding()

    private val viewModel: TransportDetailsViewModel by viewModels()

    private val args: TransportDetailsFragmentArgs by navArgs()

    private val sealsAdapter = SealsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setResultListener()
        initViews()
        bindTransport(args.transport)
        observeData()
        findNavController().enableOnBackPressed(true)
    }

    private fun setResultListener() {
        setFragmentResultListener(ResultKeys.CODE_ADD_SEAL) { _, bundle ->
            val sealNumber =
                bundle.getString(ResultKeys.SEAL_NUMBER) ?: return@setFragmentResultListener
            val sealPhotoName = bundle.getString(ResultKeys.SEAL_PHOTO_NAME)
            viewModel.addSeal(sealNumber, sealPhotoName)
        }
    }

    private fun initViews() {
        binding.addSeal.setOnClickListener {
            findNavController().navigate(
                TransportDetailsFragmentDirections.actionTransportDetailsToAddSeal()
            )
        }

        binding.submit.setOnClickListener {
            viewModel.submitTransport(args.transport)
            findNavController().popBackStack()
        }

        binding.sealsRv.adapter = sealsAdapter
        binding.sealsRv.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
    }

    private fun bindTransport(transport: Transport) {
        binding.stateNumber.text = transport.stateNumber
        binding.driverData.text = transport.driverData
        binding.trailerNumber.text =
            getString(R.string.trailer_number).format(transport.trailerNumber)
    }

    private fun observeData() {
        viewModel.sealsLiveData.observe(this) { seals ->
            sealsAdapter.submitList(seals)
        }
    }
}
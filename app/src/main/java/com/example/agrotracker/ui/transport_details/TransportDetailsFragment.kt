package com.example.agrotracker.ui.transport_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agrotracker.databinding.FragmentTransportDetailsBinding
import com.example.agrotracker.ui.transport_details.seals_adapter.SealsAdapter
import com.example.data.models.Transport
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransportDetailsFragment : Fragment() {

    private var _binding: FragmentTransportDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransportDetailsViewModel by viewModels()

    private val args: TransportDetailsFragmentArgs by navArgs()

    private val sealsAdapter = SealsAdapter { seal ->

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransportDetailsBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindTransport(args.transport)
        viewModel.sealsLiveData.observe(this) { seals ->
            sealsAdapter.submitList(seals)
        }
        findNavController().enableOnBackPressed(true)
    }

    private fun initViews() {
        binding.addSeal.setOnClickListener {
            setFragmentResultListener("add_seal") { _, bundle ->
                val sealNumber = bundle.getString("sealNumber")!!
                val imageUri = bundle.getString("imageUri")
                viewModel.addSeal(sealNumber, imageUri)
            }
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
        binding.trailerNumber.text = transport.trailerNumber
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
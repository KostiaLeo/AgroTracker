package com.example.agrotracker.ui.transport_list

import android.os.Bundle
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agrotracker.R
import com.example.agrotracker.databinding.FragmentListTransportBinding
import com.example.agrotracker.ui.transport_list.adapter.TransportsAdapter
import com.example.data.models.Transport
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransportListFragment : Fragment(R.layout.fragment_list_transport) {

    private val binding: FragmentListTransportBinding by viewBinding()

    private val viewModel: TransportListViewModel by viewModels()

    private val transportsAdapter by lazy {
        TransportsAdapter(::openDetailsScreen)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()

        viewModel.transportsLiveData.observe(this) { transports ->
            transportsAdapter.submitList(transports)
        }

        viewModel.pendingUploadsLiveData.observe(this) { isPendingWork ->
            binding.transportsRv.isVisible = !isPendingWork
            binding.transportsLabel.isVisible = !isPendingWork

            binding.progressBar.isVisible = isPendingWork
            binding.uploadingLabel.isVisible = isPendingWork
        }
    }

    private fun initViews() {
        binding.transportsRv.run {
            adapter = transportsAdapter
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        }
    }

    private fun openDetailsScreen(transport: Transport) {
        val direction = TransportListFragmentDirections
            .actionTransportListToTransportDetails(transport)
        findNavController().navigate(direction)
    }
}
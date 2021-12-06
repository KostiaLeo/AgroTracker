package com.example.agrotracker.ui.transport_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agrotracker.databinding.FragmentListTransportBinding
import com.example.agrotracker.ui.transport_list.adapter.TransportsAdapter
import com.example.data.models.Transport
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransportListFragment : Fragment() {

    private var _binding: FragmentListTransportBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransportListViewModel by viewModels()

    private val transportsAdapter by lazy {
        TransportsAdapter(::openDetailsScreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListTransportBinding.inflate(inflater, container, false)
        initViews()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.transportsLiveData.observe(this) { transports ->
            transportsAdapter.submitList(transports)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
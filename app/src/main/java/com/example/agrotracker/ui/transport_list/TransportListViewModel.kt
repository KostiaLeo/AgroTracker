package com.example.agrotracker.ui.transport_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.agrotracker.domain.LoadTransportListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransportListViewModel @Inject constructor(
    private val loadTransportUseCase: LoadTransportListUseCase
) : ViewModel() {
    val transportsLiveData =
        loadTransportUseCase.loadTransport()
            .asLiveData()
}
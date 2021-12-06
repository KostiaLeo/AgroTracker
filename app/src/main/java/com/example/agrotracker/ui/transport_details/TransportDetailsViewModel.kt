package com.example.agrotracker.ui.transport_details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.agrotracker.domain.SubmitTransportUseCase
import com.example.data.models.Fact
import com.example.data.models.Seal
import com.example.data.models.Transport
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TransportDetailsViewModel @Inject constructor(
    private val submitTransportUseCase: SubmitTransportUseCase
) : ViewModel() {

    private val _sealsLiveData = MutableLiveData<List<Seal>>()
    val sealsLiveData: LiveData<List<Seal>> get() = _sealsLiveData

    private val _errorLiveData = MutableLiveData<Throwable>()
    val errorLiveData: LiveData<Throwable> get() = _errorLiveData

    private val seals = mutableListOf<Seal>()

    fun addSeal(sealNumber: String, imageUri: String?) {
        seals.add(Seal(sealNumber, imageUri))
        _sealsLiveData.value = seals
    }

    fun submitTransport(transport: Transport) {
        val fact = Fact(transport.id, seals)
        submitTransportUseCase.submit(fact)
    }
}


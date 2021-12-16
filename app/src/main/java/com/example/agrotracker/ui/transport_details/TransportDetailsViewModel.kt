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

    private val _sealsLiveData = MutableLiveData<List<Seal>>(emptyList())
    val sealsLiveData: LiveData<List<Seal>> get() = _sealsLiveData

    private val seals = mutableListOf<Seal>()

    /**
     * @return [true] if a seal was successfully added
     * @return [false] if the seal has already been added before
     * */
    fun addSeal(sealNumber: String, photoName: String?): Boolean {
        val isAlreadyAdded = seals.any { it.sealNumber == sealNumber }
        if (isAlreadyAdded) return false

        seals.add(Seal(sealNumber, photoName))
        _sealsLiveData.value = seals

        return true
    }

    fun submitTransport(transport: Transport) {
        val fact = Fact(transport.id, transport.currentWayBill, seals)
        submitTransportUseCase(fact)
    }
}
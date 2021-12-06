package com.example.agrotracker.domain

import com.example.data.models.Fact
import com.example.data.repository.TransportsRepository
import javax.inject.Inject

interface SubmitTransportUseCase {
    fun submit(fact: Fact)
}

class SubmitTransportUseCaseDefault @Inject constructor(
    private val repository: TransportsRepository
) : SubmitTransportUseCase {
    override fun submit(fact: Fact) {
        repository.submitTransport(fact)
    }
}
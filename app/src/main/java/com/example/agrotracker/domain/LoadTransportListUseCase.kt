package com.example.agrotracker.domain

import com.example.data.models.Transport
import com.example.data.repository.TransportsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface LoadTransportListUseCase {
    operator fun invoke(): Flow<List<Transport>>
}

class LoadTransportListUseCaseDefault @Inject constructor(
    private val transportsRepository: TransportsRepository
) : LoadTransportListUseCase {
    override operator fun invoke(): Flow<List<Transport>> {
        return transportsRepository.loadTransports()
    }
}
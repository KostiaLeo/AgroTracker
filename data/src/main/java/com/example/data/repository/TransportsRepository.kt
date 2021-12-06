package com.example.data.repository

import com.example.data.api.TransportsApi
import com.example.data.mappers.TransportMapper
import com.example.data.models.Fact
import com.example.data.models.Transport
import com.example.data.utils.TransportKeys.IN_PROCESS
import com.example.data.worker.UploadDataWorkerLauncher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface TransportsRepository {
    fun loadTransports(): Flow<List<Transport>>
    fun submitTransport(fact: Fact)
}

class TransportsRepositoryDefault @Inject constructor(
    private val transportsApi: TransportsApi,
    private val uploadDataWorkerLauncher: UploadDataWorkerLauncher
) : TransportsRepository {
    override fun loadTransports(): Flow<List<Transport>> {
        return transportsApi.loadPendingTransports()
            .map { snapshot ->
                snapshot.documents.map(TransportMapper::mapDocumentToTransport)
            }
    }


    override fun submitTransport(fact: Fact) {
        transportsApi.addFact(fact)
        transportsApi.updateTransport(fact.transportId, IN_PROCESS to false)

        uploadDataWorkerLauncher.enqueueWork()
    }
}
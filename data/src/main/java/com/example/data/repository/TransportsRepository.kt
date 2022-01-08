package com.example.data.repository

import com.example.data.api.TransportsApi
import com.example.data.models.Fact
import com.example.data.models.Transport
import com.example.data.photos.PhotosStorage
import com.example.data.utils.TransportKeys.IN_PROCESS
import com.example.data.worker.UploadDataLauncher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TransportsRepository {
    fun loadTransports(): Flow<List<Transport>>
    fun submitTransport(fact: Fact)
}

class TransportsRepositoryDefault @Inject constructor(
    private val transportsApi: TransportsApi,
    private val uploadDataLauncher: UploadDataLauncher,
    private val photosStorage: PhotosStorage
) : TransportsRepository {

    override fun loadTransports(): Flow<List<Transport>> {
        return transportsApi.loadPendingTransports()
    }

    override fun submitTransport(fact: Fact) {
        addPendingPhotos(fact)
        transportsApi.addFact(fact)
        transportsApi.updateTransport(fact.transportId, IN_PROCESS to false)
        uploadDataLauncher.enqueueWork()
    }


    private fun addPendingPhotos(fact: Fact) {
        val photos = fact.seals.mapNotNull { it.photoName }
        if (photos.isNotEmpty()) {
            photosStorage.addPendingPhotos(photos)
        }
    }
}
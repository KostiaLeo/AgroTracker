package com.example.data.repository

import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.data.api.TransportsApi
import com.example.data.mappers.toTransport
import com.example.data.models.Fact
import com.example.data.models.Transport
import com.example.data.utils.SharedPreferencesKeys
import com.example.data.utils.TransportKeys.IN_PROCESS
import com.example.data.worker.UploadDataLauncher
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface TransportsRepository {
    fun loadTransports(): Flow<List<Transport>>
    fun submitTransport(fact: Fact)
}

class TransportsRepositoryDefault @Inject constructor(
    private val transportsApi: TransportsApi,
    private val uploadDataLauncher: UploadDataLauncher,
    private val sharedPreferences: SharedPreferences
) : TransportsRepository {

    override fun loadTransports(): Flow<List<Transport>> {
        return transportsApi.loadPendingTransports()
            .map { snapshot ->
                snapshot.documents.map(DocumentSnapshot::toTransport)
            }
    }

    override fun submitTransport(fact: Fact) {
        addPendingPhotos(fact)
        transportsApi.addFact(fact)
        transportsApi.updateTransport(fact.transportId, IN_PROCESS to false)
        uploadDataLauncher.enqueueWork()
    }


    private fun addPendingPhotos(fact: Fact) {
        val photoNamesSet = sharedPreferences
            .getStringSet(SharedPreferencesKeys.KEY_PHOTOS_SET, emptySet()).orEmpty().toMutableSet()

        fact.seals.mapNotNullTo(photoNamesSet) { it.photoName }

        sharedPreferences.edit {
            putStringSet(SharedPreferencesKeys.KEY_PHOTOS_SET, photoNamesSet)
        }
    }
}
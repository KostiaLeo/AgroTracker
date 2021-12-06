package com.example.data.mappers

import com.example.data.models.Transport
import com.example.data.utils.TransportKeys
import com.google.firebase.firestore.DocumentSnapshot

object TransportMapper {
    fun mapDocumentToTransport(documentSnapshot: DocumentSnapshot): Transport {
        val id = documentSnapshot.id
        val stateNumber =
            documentSnapshot[TransportKeys.STATE_NUMBER] as? String ?: error("Invalid stateNumber")
        val driverData =
            documentSnapshot[TransportKeys.DRIVER_DATA] as? String ?: error("Invalid driverData")
        val trailerNumber =
            documentSnapshot[TransportKeys.TRAILER_NUMBER] as? String ?: error("Invalid driverData")
        val inProcess =
            documentSnapshot[TransportKeys.IN_PROCESS] as? Boolean ?: error("Invalid driverData")

        return Transport(id, inProcess, stateNumber, driverData, trailerNumber)
    }
}
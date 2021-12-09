package com.example.data.mappers

import com.example.data.models.Transport
import com.example.data.utils.TransportKeys.DRIVER_DATA
import com.example.data.utils.TransportKeys.IN_PROCESS
import com.example.data.utils.TransportKeys.STATE_NUMBER
import com.example.data.utils.TransportKeys.TRAILER_NUMBER
import com.example.data.utils.getOrError
import com.google.firebase.firestore.DocumentSnapshot

fun DocumentSnapshot.toTransport(): Transport {
    val stateNumber = getOrError<String>(STATE_NUMBER)
    val driverData = getOrError<String>(DRIVER_DATA)
    val trailerNumber = getOrError<String>(TRAILER_NUMBER)
    val inProcess = getOrError<Boolean>(IN_PROCESS)

    return Transport(id, inProcess, stateNumber, driverData, trailerNumber)
}
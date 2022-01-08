package com.example.data.api

import com.example.data.mappers.toTransport
import com.example.data.models.Fact
import com.example.data.models.Transport
import com.example.data.utils.TransportKeys
import com.example.data.utils.toSnapshotFlow
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface TransportsApi {
    fun loadPendingTransports(): Flow<List<Transport>>
    fun addFact(fact: Fact)
    fun updateTransport(transportId: String, vararg values: Pair<String, Any>)
}

class FirebaseTransportsApi @Inject constructor() : TransportsApi {
    override fun loadPendingTransports(): Flow<List<Transport>> {
        return Firebase.firestore.collection(TransportKeys.TRANSPORTS_COLLECTION)
            .whereEqualTo(TransportKeys.IN_PROCESS, true)
            .toSnapshotFlow()
            .map { snapshot ->
                snapshot.documents.map(DocumentSnapshot::toTransport)
            }
    }

    override fun addFact(fact: Fact) {
        Firebase.firestore.collection(TransportKeys.FACTS_COLLECTION)
            .add(fact)
    }

    override fun updateTransport(transportId: String, vararg values: Pair<String, Any>) {
        Firebase.firestore.document("${TransportKeys.TRANSPORTS_COLLECTION}/$transportId")
            .update(hashMapOf(*values))
    }
}
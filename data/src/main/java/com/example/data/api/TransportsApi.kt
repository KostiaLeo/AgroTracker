package com.example.data.api

import com.example.data.models.Fact
import com.example.data.utils.TransportKeys
import com.example.data.utils.toSnapshotFlow
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TransportsApi {
    fun loadPendingTransports(): Flow<QuerySnapshot>
    fun addFact(fact: Fact)
    fun updateTransport(id: String, vararg values: Pair<String, Any>)
}

class FirebaseTransportsApi @Inject constructor() : TransportsApi {
    override fun loadPendingTransports(): Flow<QuerySnapshot> {
        return Firebase.firestore.collection(TransportKeys.TRANSPORTS)
            .whereEqualTo(TransportKeys.IN_PROCESS, true)
            .toSnapshotFlow()
    }

    override fun addFact(fact: Fact) {
        Firebase.firestore.collection(TransportKeys.FACTS)
            .add(fact)
    }

    override fun updateTransport(id: String, vararg values: Pair<String, Any>) {
        Firebase.firestore.document("${TransportKeys.TRANSPORTS}/$id")
            .update(hashMapOf(*values))
    }
}
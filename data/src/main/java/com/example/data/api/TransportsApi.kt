package com.example.data.api

import com.example.data.models.Fact
import com.example.data.utils.TransportKeys
import com.example.data.utils.toSnapshotFlow
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface TransportsApi {
    fun loadPendingTransports(): Flow<QuerySnapshot>
    fun addFact(fact: Fact)
    fun updateTransport(id: String, vararg values: Pair<String, Any>)
}

class FirebaseTransportsApi @Inject constructor(
    private val firestore: FirebaseFirestore
) : TransportsApi {
    override fun loadPendingTransports(): Flow<QuerySnapshot> {
        return firestore.collection(TransportKeys.TRANSPORTS)
            .whereEqualTo(TransportKeys.IN_PROCESS, true)
            .toSnapshotFlow()
    }

    override fun addFact(fact: Fact) {
        firestore.collection(TransportKeys.FACTS)
            .add(fact)
    }

    override fun updateTransport(id: String, vararg values: Pair<String, Any>) {
        firestore.document("${TransportKeys.TRANSPORTS}/$id")
            .update(hashMapOf(*values))
    }
}
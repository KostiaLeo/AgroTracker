package com.example.data.utils

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

fun Query.toSnapshotFlow(): Flow<QuerySnapshot> {
    return callbackFlow {
        val registration = addSnapshotListener { snapshot, exception ->
            exception?.let(::error)
            snapshot?.let(::trySend)
        }
        awaitClose { registration.remove() }
    }
}

fun DocumentReference.toSnapshotFlow(): Flow<DocumentSnapshot> {
    return callbackFlow {
        val registration = addSnapshotListener { document, exception ->
            exception?.let(::error)
            document?.let(::trySend)
        }
        awaitClose { registration.remove() }
    }
}

suspend fun <T> Task<T>.await(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener(continuation::resume)
        addOnFailureListener(continuation::resumeWithException)
    }
}
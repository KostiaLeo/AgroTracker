package com.example.data

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot

val testUri: Uri = Uri.EMPTY

inline fun <reified T : Task<R>, R> mockTask(result: R? = null, error: Exception? = null): T {
    val mockTask = mockk<T>(relaxed = true)
    val successSlot = slot<OnSuccessListener<R>>()
    val errorSlot = slot<OnFailureListener>()

    every {
        mockTask.addOnSuccessListener(capture(successSlot))
        mockTask.addOnFailureListener(capture(errorSlot))
    } answers {
        result?.let(successSlot.captured::onSuccess)
        error?.let(errorSlot.captured::onFailure)
        mockTask
    }

    return mockTask
}
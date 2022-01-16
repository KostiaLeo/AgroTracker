package com.example.data.utils

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic

object FirebaseTestUtils {
    fun mockSuccessFirebaseStorage(): StorageReference {
        val mockStorageReference = mockk<StorageReference>(relaxed = true)
        every { mockStorageReference.child(any()) } returns mockStorageReference
        every { mockStorageReference.putFile(any()) } returns mockTask(result = mockk())

        val mockFirebaseStorage = mockk<FirebaseStorage>(relaxed = true)
        every { mockFirebaseStorage.reference } returns mockStorageReference

        mockkStatic(FirebaseStorage::class)
        every { FirebaseStorage.getInstance() } returns mockFirebaseStorage

        return mockStorageReference
    }
}
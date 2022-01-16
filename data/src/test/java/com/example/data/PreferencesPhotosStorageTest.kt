package com.example.data

import android.content.SharedPreferences
import android.net.Uri
import com.example.data.photos.PreferencesPhotosStorage
import com.example.data.utils.SharedPreferencesKeys
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.File

class PreferencesPhotosStorageTest {

    @MockK(relaxed = true)
    lateinit var sharedPreferences: SharedPreferences

    @MockK(relaxed = true)
    lateinit var sharedPreferencesEditor: SharedPreferences.Editor

    private lateinit var storageDir: File

    private lateinit var photosStorage: PreferencesPhotosStorage

    private val key = SharedPreferencesKeys.KEY_PHOTOS_SET

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { sharedPreferences.edit() } returns sharedPreferencesEditor
        storageDir = File("testFile")

        photosStorage = PreferencesPhotosStorage(sharedPreferences, storageDir)
    }

    @After
    fun tearDown() {
        storageDir.delete()
        unmockkAll()
    }

    @Test
    fun `test adding photos to empty storage`() {
        every { sharedPreferences.getStringSet(eq(key), any()) } returns emptySet()

        photosStorage.addPendingPhotos(listOf("photo1", "photo2"))

        verifyOrder {
            sharedPreferences.getStringSet(key, emptySet())
            sharedPreferencesEditor.putStringSet(key, setOf("photo1", "photo2"))
        }
    }

    @Test
    fun `test adding photos to filled storage`() {
        every { sharedPreferences.getStringSet(eq(key), any()) } returns setOf("photo1", "photo2")

        photosStorage.addPendingPhotos(listOf("photo3", "photo4"))

        verifyOrder {
            sharedPreferences.getStringSet(key, emptySet())
            sharedPreferencesEditor.putStringSet(key, setOf("photo1", "photo2", "photo3", "photo4"))
        }
    }

    @Test
    fun `test adding empty photos list to filled storage`() {
        every { sharedPreferences.getStringSet(eq(key), any()) } returns setOf("photo1", "photo2")

        photosStorage.addPendingPhotos(emptyList())

        verifyOrder {
            sharedPreferences.getStringSet(key, emptySet())
            sharedPreferencesEditor.putStringSet(key, setOf("photo1", "photo2"))
        }
    }

    @Test
    fun `test success uploading photos`() = runBlocking {
        val fileName1 = "photo1"
        val fileName2 = "photo2"
        every { sharedPreferences.getStringSet(eq(key), any()) } returns setOf(fileName1, fileName2)
        val fileUris = mockSuccessFilesUri(fileName1, fileName2)
        val mockStorageReference = mockSuccessFirebaseStorage()

        photosStorage.uploadPendingPhotos()

        verify { sharedPreferences.getStringSet(key, emptySet()) }
        verify { sharedPreferencesEditor.putStringSet(key, emptySet()) }
        verifyOrder {
            mockStorageReference.child(fileName1)
            mockStorageReference.putFile(fileUris[0])
        }
        verifyOrder {
            mockStorageReference.child(fileName2)
            mockStorageReference.putFile(fileUris[1])
        }
    }

    private fun mockSuccessFilesUri(vararg photoNames: String): List<Uri> {
        mockkStatic(Uri::class)

        return photoNames.map {
            val file = File(storageDir, it)
            val uri = mockk<Uri>()
            every { Uri.fromFile(file) } returns uri
            uri
        }
    }

    private fun mockSuccessFirebaseStorage(): StorageReference {
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
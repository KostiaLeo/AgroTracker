package com.example.data

import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import com.example.data.photos.PreferencesPhotosStorage
import com.example.data.utils.FirebaseTestUtils
import com.example.data.utils.SharedPreferencesKeys
import com.example.data.utils.mockTask
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.hamcrest.core.StringStartsWith.startsWith
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
        val (fileName1, fileName2) = "photo1" to "photo2"
        every { sharedPreferences.getStringSet(eq(key), any()) } returns setOf(fileName1, fileName2)
        val fileUris = mockSuccessFilesUri(fileName1, fileName2)
        val mockStorageReference = FirebaseTestUtils.mockSuccessFirebaseStorage()

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

    @Test
    fun `test 2 out of 3 success uploading`() = runBlocking {
        val fileName1 = "photo1"
        val fileName2 = "photo2"
        val fileName3 = "photo3"
        every { sharedPreferences.getStringSet(eq(key), any()) } returns setOf(
            fileName1,
            fileName2,
            fileName3
        )
        val fileUris = mockSuccessFilesUri(fileName1, fileName2, fileName3)
        val mockStorageReference = FirebaseTestUtils.mockSuccessFirebaseStorage()
        // mock failed uploading of 3rd image
        every { mockStorageReference.putFile(fileUris[2]) } returns mockTask(
            error = IllegalStateException(
                "error uploading photo"
            )
        )
        mockkStatic(Log::class)

        photosStorage.uploadPendingPhotos()

        verify { sharedPreferences.getStringSet(key, emptySet()) }
        verify { sharedPreferencesEditor.putStringSet(key, setOf(fileName3)) }
        listOf(fileName1, fileName2, fileName3).forEachIndexed { index, fileName ->
            verifyOrder {
                mockStorageReference.child(fileName)
                mockStorageReference.putFile(fileUris[index])
            }
        }
        verify { Log.e(any(), any(), any<IllegalStateException>()) }
    }

    @Test
    fun `test no success uploading`() = runBlocking {
        val fileName1 = "photo1"
        val fileName2 = "photo2"
        val fileName3 = "photo3"
        every { sharedPreferences.getStringSet(eq(key), any()) } returns setOf(
            fileName1,
            fileName2,
            fileName3
        )
        val fileUris = mockSuccessFilesUri(fileName1, fileName2, fileName3)
        val mockStorageReference = FirebaseTestUtils.mockSuccessFirebaseStorage()
        // mock failed uploading of all images
        every { mockStorageReference.putFile(any()) } returns mockTask(
            error = IllegalStateException(
                "error uploading photo"
            )
        )
        mockkStatic(Log::class)

        photosStorage.uploadPendingPhotos()

        verify { sharedPreferences.getStringSet(key, emptySet()) }
        verify { sharedPreferencesEditor.putStringSet(key, setOf(fileName1, fileName2, fileName3)) }
        listOf(fileName1, fileName2, fileName3).forEachIndexed { index, fileName ->
            verifyOrder {
                mockStorageReference.child(fileName)
                mockStorageReference.putFile(fileUris[index])
            }
        }
        verify(exactly = 3) { Log.e(any(), any(), any<IllegalStateException>()) }
    }


    @Test
    fun `test creating an image file`() {
        mockkStatic(File::class)
        every { File.createTempFile(any(), any(), any()) } returns mockk()

        val file = photosStorage.createImageFile()

        verify {
            File.createTempFile(
                match(startsWith("JPEG_")::matches),
                eq(".jpg"),
                eq(storageDir)
            )
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
}
package com.example.data.worker

import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import com.example.data.photos.PhotosUploader
import com.example.data.utils.mockTask
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class UploadDataWorkerTest {
    private lateinit var worker: UploadDataWorker

    @MockK(relaxUnitFun = true)
    lateinit var photosUploader: PhotosUploader

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        worker = TestListenableWorkerBuilder<UploadDataWorker>(RuntimeEnvironment.getApplication())
            .setWorkerFactory(TestWorkerFactory(photosUploader))
            .build()
    }

    @Test
    fun `test success uploading`() = runBlocking {
        val mockFirestore = mockk<FirebaseFirestore>(relaxUnitFun = true)
        every { mockFirestore.enableNetwork() } returns mockTask(mockk())
        every { mockFirestore.waitForPendingWrites() } returns mockTask(mockk())
        mockkStatic(FirebaseFirestore::class)
        every { FirebaseFirestore.getInstance() } returns mockFirestore

        val result = worker.doWork()
        assertThat(result, `is`(ListenableWorker.Result.success()))

        coVerify { photosUploader.uploadPendingPhotos() }
        verifyAll {
            mockFirestore.enableNetwork()
            mockFirestore.waitForPendingWrites()
        }
    }
}
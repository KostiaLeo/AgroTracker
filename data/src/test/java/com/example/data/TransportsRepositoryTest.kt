package com.example.data

import com.example.data.api.TransportsApi
import com.example.data.models.Fact
import com.example.data.models.Seal
import com.example.data.models.Transport
import com.example.data.photos.PhotosStorage
import com.example.data.repository.TransportsRepositoryDefault
import com.example.data.utils.TransportKeys.IN_PROCESS
import com.example.data.worker.UploadDataLauncher
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class TransportsRepositoryTest {

    private lateinit var repository: TransportsRepositoryDefault

    @MockK(relaxed = true)
    lateinit var transportsApi: TransportsApi

    @MockK(relaxUnitFun = true)
    lateinit var uploadDataLauncher: UploadDataLauncher

    @MockK(relaxed = true)
    lateinit var photosStorage: PhotosStorage

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        repository = TransportsRepositoryDefault(transportsApi, uploadDataLauncher, photosStorage)
    }

    @Test
    fun `test load transports success`() = runBlocking {
        every { transportsApi.loadPendingTransports() } returns flowOf(successTransportList)

        val actual = repository.loadTransports().first()
        val expected = successTransportList
        assertEquals("Failed loading transports on success flow", expected, actual)

        verify { transportsApi.loadPendingTransports() }
    }

    @Test
    fun `test load empty transports`() = runBlocking {
        every { transportsApi.loadPendingTransports() } returns flowOf(emptyList())

        val actual = repository.loadTransports().first()
        val expected = emptyList<Transport>()
        assertEquals("Failed loading empty transports", expected, actual)

        verify { transportsApi.loadPendingTransports() }
    }

    @Test
    fun `test submit transport success`() {
        val expected = successFact
        repository.submitTransport(expected)

        verifyOrder {
            photosStorage.addPendingPhotos(listOf("photoname1"))

            transportsApi.addFact(expected)
            transportsApi.updateTransport(expected.transportId, IN_PROCESS to false)

            uploadDataLauncher.enqueueWork()
        }
    }

    @Test
    fun `test submit transport without photos`() {
        val expected =
            successFact.copy(seals = listOf(Seal("A11111111", null), Seal("A11111111", null)))
        repository.submitTransport(expected)

        verifyOrder {
            transportsApi.addFact(expected)
            transportsApi.updateTransport(expected.transportId, IN_PROCESS to false)

            uploadDataLauncher.enqueueWork()

            photosStorage wasNot Called
        }
    }


    private val successFact
        get() = Fact(
            "id1",
            "TR11111111",
            listOf(Seal("A11111111", "photoname1"), Seal("A11111111", null))
        )

    private val successTransportList: List<Transport>
        get() = listOf(
            Transport("id1", true, "AB1111AB", "Іван Іван Іван1", "AA0000BB", "TR11111111"),
            Transport("id2", true, "AB2222AB", "Іван Іван Іван2", "AA0000BB", "TR11111112"),
            Transport("id3", true, "AB3333AB", "Іван Іван Іван3", "AA0000BB", "TR11111113")
        )
}
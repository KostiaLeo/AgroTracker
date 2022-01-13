package com.example.data

import android.graphics.Rect
import android.net.Uri
import android.util.Log
import com.example.data.SealRecognizerTestUtils.buildMockText
import com.example.data.SealRecognizerTestUtils.mockErrorTextRecognizer
import com.example.data.SealRecognizerTestUtils.mockSuccessInputImage
import com.example.data.SealRecognizerTestUtils.mockSuccessTextRecognizer
import com.example.data.recognizer.OfflineSealRecognizer
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SealRecognizerTest {

    @MockK
    lateinit var textRecognizer: TextRecognizer

    private lateinit var sealRecognizer: OfflineSealRecognizer

    private val testUri = Uri.EMPTY

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sealRecognizer = OfflineSealRecognizer(mockk(), textRecognizer)
    }

    @Test
    fun `test success recognition from 3 elements`() = runBlocking {
        val expectedResult = "A11111111"
        val mockText = buildMockText("B09", "0398jdpcw0ev-", expectedResult)

        val image = mockk<InputImage>()
        mockSuccessInputImage(image)
        mockSuccessTextRecognizer(textRecognizer, mockText)

        val actual = sealRecognizer.recognize(testUri)
        assertEquals(expectedResult, actual)

        verify { textRecognizer.process(image) }
    }

    @Test
    fun `test several suitable items`() = runBlocking {
        val expectedResult = "A11111111"
        val mockText =
            buildMockText("B09", "0398jdpcw0ev-", expectedResult, "B00000000", "C12341234")

        val image = mockk<InputImage>()
        mockSuccessInputImage(image)
        mockSuccessTextRecognizer(textRecognizer, mockText)

        val actual = sealRecognizer.recognize(testUri)
        assertEquals(expectedResult, actual)

        verify { textRecognizer.process(image) }
    }

    @Test
    fun `test no suitable items`() = runBlocking {
        val mockText = buildMockText("B09", "0398jdpcw0ev-", "A1111")

        val image = mockk<InputImage>()
        mockSuccessInputImage(image)
        mockSuccessTextRecognizer(textRecognizer, mockText)

        val actual = sealRecognizer.recognize(testUri)
        assertNull(actual)

        verify { textRecognizer.process(image) }
    }

    @Test
    fun `test no items`() = runBlocking {
        val mockText = buildMockText()

        val image = mockk<InputImage>()
        mockSuccessInputImage(image)
        mockSuccessTextRecognizer(textRecognizer, mockText)

        val actual = sealRecognizer.recognize(testUri)
        assertNull(actual)

        verify { textRecognizer.process(image) }
    }

    @Test
    fun `test resolving image failed`() = runBlocking {
        mockkStatic(InputImage::class)
        val exception = Exception("Error")
        every { InputImage.fromFilePath(any(), any()) } throws exception

        mockkStatic(Log::class)

        val actual = sealRecognizer.recognize(testUri)
        assertNull(actual)

        verifyAll {
            Log.e(eq("SealRecognizer"), any(), eq(exception))
            textRecognizer wasNot Called
        }
    }

    @Test
    fun `test recognition failed`() = runBlocking {
        val image = mockk<InputImage>()
        mockSuccessInputImage(image)
        mockkStatic(Log::class)

        val exception = Exception("Error")
        mockErrorTextRecognizer(textRecognizer, exception)

        val actual = sealRecognizer.recognize(testUri)
        assertNull(actual)

        verifyAll {
            textRecognizer.process(image)
            Log.e(eq("SealRecognizer"), any(), any<Exception>())
        }
    }
}

object SealRecognizerTestUtils {
    fun mockSuccessInputImage(mockImage: InputImage = mockk(), uri: Uri = Uri.EMPTY) {
        mockkStatic(InputImage::class)
        every { InputImage.fromFilePath(any(), eq(uri)) } returns mockImage
    }

    fun mockSuccessTextRecognizer(textRecognizer: TextRecognizer, resultText: Text) {
        val mockTask = mockk<Task<Text>>(relaxed = true)
        val slot = slot<OnSuccessListener<Text>>()
        every {
            mockTask.addOnSuccessListener(capture(slot))
        } answers {
            slot.captured.onSuccess(resultText)
            mockTask
        }

        every { textRecognizer.process(any<InputImage>()) } returns mockTask
    }

    fun mockErrorTextRecognizer(textRecognizer: TextRecognizer, exception: Exception) {
        val mockTask = mockk<Task<Text>>(relaxed = true)
        val slot = slot<OnFailureListener>()
        every {
            mockTask.addOnFailureListener(capture(slot))
        } answers {
            slot.captured.onFailure(exception)
            mockTask
        }

        every { textRecognizer.process(any<InputImage>()) } returns mockTask
    }

    fun buildMockText(vararg elements: String): Text {
        return Text(
            "0",
            listOf(
                Text.TextBlock(
                    "1",
                    Rect(),
                    emptyList(),
                    "2",
                    listOf(
                        Text.Line(
                            "1",
                            Rect(),
                            emptyList(),
                            "",
                            elements.map { Text.Element(it, Rect(), emptyList(), "") })
                    )
                )
            )
        )
    }
}
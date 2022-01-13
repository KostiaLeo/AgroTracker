package com.example.data

import android.graphics.Rect
import android.net.Uri
import com.example.data.recognizer.OfflineSealRecognizer
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognizer
import io.mockk.*
import io.mockk.impl.annotations.MockK
import junit.framework.TestCase.assertEquals
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

        mockInputImage()
        mockTextRecognizer(mockText)

        val actual = sealRecognizer.recognize(testUri)
        assertEquals(expectedResult, actual)
    }


    private fun mockInputImage(mockImage: InputImage = mockk(), uri: Uri = testUri) {
        mockkStatic(InputImage::class)
        every { InputImage.fromFilePath(any(), eq(uri)) } returns mockImage
    }

    private fun mockTextRecognizer(resultText: Text) {
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

    private fun buildMockText(vararg elements: String): Text {
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
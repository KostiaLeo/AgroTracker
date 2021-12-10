package com.example.agrotracker.ui.add_seal

import android.net.Uri
import android.util.Log
import androidx.core.net.toFile
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrotracker.domain.RecognizeSealUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSealViewModel @Inject constructor(
    private val recognizeSealUseCase: RecognizeSealUseCase
) : ViewModel() {

    private val _recognitionStateLiveData = MutableLiveData<RecognitionState>()
    val recognitionStateLiveData: LiveData<RecognitionState> get() = _recognitionStateLiveData

    private val recognitionErrorHandler = CoroutineExceptionHandler { _, throwable ->
        logError(throwable)
        _recognitionStateLiveData.postValue(RecognitionState.Error)
    }


    fun recognizeSealNumber(uri: Uri) {
        viewModelScope.launch(recognitionErrorHandler) {
            _recognitionStateLiveData.value = RecognitionState.Loading

            val sealNumber = recognizeSealUseCase(uri)

            _recognitionStateLiveData.value = if (sealNumber != null) {
                RecognitionState.Success(RecognitionResult(sealNumber, uri))
            } else {
                // there's no need to keep photo if it was invalid
                removePhoto(uri)

                RecognitionState.Error
            }
        }
    }

    private fun removePhoto(uri: Uri?) {
        val errorHandler = CoroutineExceptionHandler { _, throwable -> logError(throwable) }

        viewModelScope.launch(Dispatchers.IO + errorHandler) {
            uri?.toFile()?.delete()
        }
    }

    override fun onCleared() {
        super.onCleared()
        recognizeSealUseCase.close()
    }


    private fun logError(
        throwable: Throwable,
        message: String = "Error: ${throwable.localizedMessage}"
    ) {
        Log.e("AddSeal", message, throwable)
    }
}
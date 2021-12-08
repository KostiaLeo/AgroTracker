package com.example.agrotracker.ui.add_seal

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.agrotracker.helpers.SealRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSealViewModel @Inject constructor(
    private val sealRecognizer: SealRecognizer
) : ViewModel() {

    private val _sealRecognitionStateLiveData = MutableLiveData<SealRecognitionState>()
    val sealNumberRecognitionStateLiveData: LiveData<SealRecognitionState> get() = _sealRecognitionStateLiveData

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        logError(throwable)
        _sealRecognitionStateLiveData.postValue(SealRecognitionState.Error(throwable))
    }


    fun recognizeSealNumber(uri: Uri) {
        viewModelScope.launch(errorHandler) {
            _sealRecognitionStateLiveData.value = SealRecognitionState.Loading

            val sealNumber = sealRecognizer.recognize(uri)

            _sealRecognitionStateLiveData.value = if (sealNumber != null) {
                SealRecognitionState.Success(RecognitionResult(sealNumber, uri))
            } else {
                SealRecognitionState.Error(IllegalStateException("Seal number is not recognized"))
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        sealRecognizer.close()
    }


    private fun logError(
        throwable: Throwable,
        message: String = "Error: ${throwable.localizedMessage}"
    ) {
        Log.e("AddSeal", message, throwable)
    }
}
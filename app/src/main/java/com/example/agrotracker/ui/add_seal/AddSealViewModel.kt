package com.example.agrotracker.ui.add_seal

import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.example.agrotracker.helpers.SealNumberRecognizer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddSealViewModel @Inject constructor(
    private val sealNumberRecognizer: SealNumberRecognizer
) : ViewModel() {

    private val _sealNumberRecognitionStateLiveData = MutableLiveData<AddSealState>()
    val sealNumberRecognitionStateLiveData: LiveData<AddSealState> get() = _sealNumberRecognitionStateLiveData

    private val errorHandler = CoroutineExceptionHandler { _, throwable ->
        logError(throwable)
        _sealNumberRecognitionStateLiveData.postValue(AddSealState.Error(throwable))
    }


    fun proceedPhoto(uri: Uri) {
        viewModelScope.launch(errorHandler) {
            _sealNumberRecognitionStateLiveData.value = AddSealState.Loading
            val sealNumber = sealNumberRecognizer.recognize(uri)
            _sealNumberRecognitionStateLiveData.value = AddSealState.Success(RecognitionResult(sealNumber, uri))
        }
    }

    override fun onCleared() {
        super.onCleared()
        sealNumberRecognizer.close()
    }


    private fun logError(
        throwable: Throwable,
        message: String = "Error: ${throwable.localizedMessage}"
    ) {
        Log.e("AddSealFragment", message, throwable)
    }
}
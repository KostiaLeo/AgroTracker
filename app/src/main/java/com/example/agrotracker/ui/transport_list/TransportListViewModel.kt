package com.example.agrotracker.ui.transport_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.agrotracker.domain.LoadTransportListUseCase
import com.example.data.worker.UploadDataWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

@HiltViewModel
class TransportListViewModel @Inject constructor(
    loadTransportUseCase: LoadTransportListUseCase,
    workManager: WorkManager
) : ViewModel() {
    val transportsLiveData = loadTransportUseCase()
        .catch { emit(emptyList()) }
        .asLiveData()

    // a progress is shown until data uploading is finished
    val pendingUploadsLiveData = workManager.getWorkInfosByTagLiveData(UploadDataWorker.TAG)
        .map { works -> works.any { it.state == WorkInfo.State.RUNNING } }
}
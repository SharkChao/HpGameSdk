package com.hupu.gamesdk.certification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.collectLatest

internal class HpCertificationViewModel: ViewModel() {
    private val repository = HpCertificationRepository()

    fun checkCertification(puid: String?) = liveData{
        repository.checkCertification(puid).collectLatest {
            emit(it)
        }
    }

    fun postCertification(puid: String?,name: String?,card: String?) = liveData {
        repository.postCertification(puid, name, card).collectLatest {
            emit(it)
        }
    }
}
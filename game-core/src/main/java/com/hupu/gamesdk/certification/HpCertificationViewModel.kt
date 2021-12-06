package com.hupu.gamesdk.certification

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

internal class HpCertificationViewModel: ViewModel() {
    private val repository = HpCertificationRepository()
    private val mainScope = MainScope()

    fun checkCertification(puid: String?): MutableLiveData<CertificationResult?> {
        val checkLiveData = MutableLiveData<CertificationResult?>()
        mainScope.launch {
            repository.checkCertification(puid).collectLatest {
                checkLiveData.postValue(it)
            }
        }
        return checkLiveData
    }

    fun postCertification(puid: String?,name: String?,card: String?): MutableLiveData<CertificationResult?> {
        val resultLiveData = MutableLiveData<CertificationResult?>()
        mainScope.launch {
            repository.postCertification(puid, name, card).collectLatest {
               resultLiveData.postValue(it)
            }
        }
        return resultLiveData
    }

    override fun onCleared() {
        super.onCleared()
        mainScope.cancel()
    }
}
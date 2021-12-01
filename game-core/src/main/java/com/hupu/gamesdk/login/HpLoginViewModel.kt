package com.hupu.gamesdk.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.flow.collectLatest

internal class HpLoginViewModel: ViewModel() {
    private val repository = HpLoginRepository()

    fun checkAccessToken() = liveData{
        repository.checkAccessToken().collectLatest {
            emit(it)
        }
    }

}
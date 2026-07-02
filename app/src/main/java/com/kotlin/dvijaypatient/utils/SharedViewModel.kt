package com.kotlin.dvijaypatient.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _signatureCallback = MutableLiveData<FragmentCalback>()
    val signatureCallback: LiveData<FragmentCalback> get() = _signatureCallback

    fun setSignatureCallback(callback: FragmentCalback) {
        _signatureCallback.value = callback
    }
}
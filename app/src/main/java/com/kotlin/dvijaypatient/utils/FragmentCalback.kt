package com.kotlin.dvijaypatient.utils

interface FragmentCalback {
    fun sendMessageToParent(isSuccess: Boolean, imageName: String)
}
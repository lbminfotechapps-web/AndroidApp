package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class BaseTargetResponse<T>(
    @SerializedName("status") val status: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: T

)
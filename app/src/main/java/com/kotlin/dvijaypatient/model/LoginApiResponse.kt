package com.kotlin.dvijaypatient.model

data class LoginApiResponse(
    val status: Boolean,
    val message: String,
    val patient_id: String,
    val patient_name: String,
    val patient_mobile: String,
    val branch_id: String,
    val branch_name: String,
    val branch_mobile: String,
    val otp: String


)
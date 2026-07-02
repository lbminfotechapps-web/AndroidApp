package com.kotlin.dvijaypatient.model
data class MedicineCheckModel(
    val name: String,
    var isTaken: String = ""   // "Yes" or "No"
)
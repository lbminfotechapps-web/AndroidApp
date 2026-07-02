package com.kotlin.dvijaypatient.model

data class MedicalDetailsResponse(
    var sugar: List<SugarResponse>,
    var weight: List<WeightResponse>,
    var bp: List<BPResponse>

)

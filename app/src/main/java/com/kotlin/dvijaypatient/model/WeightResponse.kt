package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class WeightResponse(
    @SerializedName("fld_date") var fld_date: String,
    @SerializedName("fld_time") var fld_time: String,
    @SerializedName("fld_value") var fld_value: String,

)

package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class BPResponse(
    @SerializedName("fld_date") var fld_date: String,
    @SerializedName("fld_time") var fld_time: String,
    @SerializedName("fld_value") var fld_value  : String,
    @SerializedName("fld_dia_value") var fld_dia_value  : String,
    @SerializedName("fld_pulse_value") var fld_pulse_value  : String,
)

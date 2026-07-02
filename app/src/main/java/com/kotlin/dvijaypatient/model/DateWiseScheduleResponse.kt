package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class DateWiseScheduleResponse(
    @SerializedName("fld_schedule_id") var fld_schedule_id: String,
    @SerializedName("fld_schedule_date") var fld_schedule_date: String,
    @SerializedName("fld_prod_name") var fld_prod_name: String,
    @SerializedName("fld_time") var fld_time: String,
    @SerializedName("fld_type") var fld_type: String,
    @SerializedName("fld_times_per_day")  var fld_times_per_day: String,
    @SerializedName("fld_status")  var fld_status: String,
    @SerializedName("fld_dose_unit")  var fld_dose_unit: String,

    )
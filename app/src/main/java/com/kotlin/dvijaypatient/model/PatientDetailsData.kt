package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class PatientDetailsData(
    @SerializedName("message") var message: String,
    @SerializedName("from_date") var start_date: String,
    @SerializedName("to_date") var end_date: String,
    @SerializedName("pills_count") var pills_count: String,
    @SerializedName("dose_unit") var dose_unit: String,
    @SerializedName("times_per_day") var times_per_day: String,
    @SerializedName("interval_value") var interval_value: String,
    @SerializedName("type") var type: String,
    @SerializedName("status") var status: Boolean,
    @SerializedName("times") var schedule_time: List<String>,
    @SerializedName("week_days") var week_days: List<String>
)

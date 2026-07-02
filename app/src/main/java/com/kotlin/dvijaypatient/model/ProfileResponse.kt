package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class ProfileResponse(
@SerializedName("patient_id") var patient_id: String,
@SerializedName("patient_name") var patient_name: String,
@SerializedName("patient_title") var patient_title: String,
@SerializedName("gender") var gender: String,
@SerializedName("birth_date") var birth_date: String,
@SerializedName("mobile") var mobile: String,
@SerializedName("mobile2") var mobile2: String,
@SerializedName("parent_mobile") var parent_mobile: String,
@SerializedName("relationship") var relationship: String,
@SerializedName("email") var email: String,
@SerializedName("state") var state: String,
@SerializedName("district") var district: String,
@SerializedName("taluka") var taluka: String,
@SerializedName("city") var city: String,
@SerializedName("address") var address: String,
@SerializedName("weight") var weight: String,
@SerializedName("prescription_att") var prescription_att: String,
@SerializedName("ref_doctor") var ref_doctor: String,
@SerializedName("branch_id") var branch_id: String,
@SerializedName("reminder") var reminder: String,
@SerializedName("is_closed") var is_closed: String,
@SerializedName("closed_reason") var closed_reason: String,
@SerializedName("closed_date") var closed_date: String,
@SerializedName("medica_patient_id") var medica_patient_id: String,
@SerializedName("medica_cc_id") var medica_cc_id: String,
@SerializedName("medica_doct_id") var medica_doct_id: String,
@SerializedName("from_date") var from_date: String,
@SerializedName("to_date") var to_date: String,
@SerializedName("frequency") var frequency: String,
@SerializedName("days") var days: String
)
package com.kotlin.dvijaypatient.model

data class MedicineModel(
    val fld_prod_name: String,
    val fld_frequency: String,
    var fld_days: String,
    var fld_qty: String,
    var startSlot: String,   // Morning / Afternoon / Night
    var time: String,
    var fld_from_date: String,
    var fld_to_date: String,
    var fld_product_id: String
)
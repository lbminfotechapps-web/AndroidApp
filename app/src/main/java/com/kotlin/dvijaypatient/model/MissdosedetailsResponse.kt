package com.kotlin.dvijaypatient.model

data class MissdosedetailsResponse(

    val fld_schedule_id: String,
    val fld_product_id: String,
    val fld_prod_name: String,
    val fld_schedule_date: String,
    val fld_time: String,
    var isTaken: String,
    val fld_status: String,
)

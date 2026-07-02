package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class PurchasedDetailsResponse(

    @SerializedName("fld_Qty")
    var fld_Qty: String,


    @SerializedName("fld_Rate")
    var fld_Rate: String,


    @SerializedName("fld_Amt")
    var fld_Amt: String,


    @SerializedName("fld_prod_name")
    var fld_prod_name: String,


    @SerializedName("fld_prod_code")
    var fld_prod_code: String

)
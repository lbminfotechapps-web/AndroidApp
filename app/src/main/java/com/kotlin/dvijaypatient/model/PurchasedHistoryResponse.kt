package com.kotlin.dvijaypatient.model

import com.google.gson.annotations.SerializedName

data class PurchasedHistoryResponse(
    @SerializedName("InvDate") var InvDate: String,
    @SerializedName("Prefix") var Prefix: String,
    @SerializedName("InvNo") var InvNo: String,
    @SerializedName("InvAmt") var InvAmt: String,
    @SerializedName("BrchId") var BrchId: String,
    @SerializedName("fld_branch_name") var fld_branch_name: String,
    @SerializedName("details")
    var details: List<PurchasedDetailsResponse>


)

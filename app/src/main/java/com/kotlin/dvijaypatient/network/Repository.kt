package com.kotlin.dvijaypatient.network

import android.util.Log
import com.kotlin.dvijaypatient.model.BaseTargetResponse
import com.kotlin.dvijaypatient.model.MedicineModel
import com.kotlin.dvijaypatient.model.ProfileResponse
import com.kotlin.dvijaypatient.model.PurchasedHistoryResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance.api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


open class Repository(private val apiService: ApiInterface) {






    fun getProfile(
        params: Map<String, String>,
        onResult: (BaseTargetResponse<ProfileResponse>?) -> Unit
    ) {
        api.patient_profile(params)
            .enqueue(object : Callback<BaseTargetResponse<ProfileResponse>> {

                override fun onResponse(
                    call: Call<BaseTargetResponse<ProfileResponse>>,
                    response: Response<BaseTargetResponse<ProfileResponse>>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Log.e("success", "Profile loaded")
                        onResult(response.body())
                    } else {
                        onResult(null)
                    }
                }

                override fun onFailure(
                    call: Call<BaseTargetResponse<ProfileResponse>>,
                    t: Throwable
                ) {
                    t.printStackTrace()
                    onResult(null)
                }
            })
    }


    fun patient_product_list(
        params: Map<String, String>,
        onResult: (BaseTargetResponse<List<MedicineModel>>?) -> Unit
    ) {
        api.getMedicineList(params).enqueue(object : Callback<BaseTargetResponse<List<MedicineModel>>> {
            override fun onResponse(
                call: Call<BaseTargetResponse<List<MedicineModel>>>,
                response: Response<BaseTargetResponse<List<MedicineModel>>>
            ) {
                if (response.isSuccessful) {
                    Log.e("sucess11","fkwhuif")

                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<BaseTargetResponse<List<MedicineModel>>>, t: Throwable) {
                onResult(null)
            }
        })
    }

    fun getPatientInvoiceHistory(
        params: Map<String, String>,
        onResult: (BaseTargetResponse<List<PurchasedHistoryResponse>>?) -> Unit
    ) {
        api.getPatientInvoiceHistory(params).enqueue(object : Callback<BaseTargetResponse<List<PurchasedHistoryResponse>>> {
            override fun onResponse(
                call: Call<BaseTargetResponse<List<PurchasedHistoryResponse>>>,
                response: Response<BaseTargetResponse<List<PurchasedHistoryResponse>>>
            ) {
                if (response.isSuccessful) {
                    Log.e("sucess11","fkwhuif")

                    onResult(response.body())
                } else {
                    onResult(null)
                }
            }

            override fun onFailure(call: Call<BaseTargetResponse<List<PurchasedHistoryResponse>>>, t: Throwable) {
                onResult(null)
            }
        })
    }










}

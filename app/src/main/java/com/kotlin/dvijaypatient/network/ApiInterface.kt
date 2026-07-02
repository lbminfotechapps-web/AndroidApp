package com.kotlin.dvijaypatient.network

import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.BaseTargetResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.DateWiseScheduleResponse
import com.kotlin.dvijaypatient.model.DocumentsModel
import com.kotlin.dvijaypatient.model.LoginApiResponse
import com.kotlin.dvijaypatient.model.MediaModel
import com.kotlin.dvijaypatient.model.MedicalDetailsResponse
import com.kotlin.dvijaypatient.model.MedicineModel
import com.kotlin.dvijaypatient.model.MissdosedetailsResponse
import com.kotlin.dvijaypatient.model.MissingdatesResponse
import com.kotlin.dvijaypatient.model.PatientDetailsData
import com.kotlin.dvijaypatient.model.ProfileResponse
import com.kotlin.dvijaypatient.model.PurchasedHistoryResponse
import com.kotlin.dvijaypatient.model.SimpleResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.PartMap


interface ApiInterface {


    @FormUrlEncoded
    @POST("patient_profile")
    fun patient_profile(@FieldMap params: Map<String, String>): Call<BaseTargetResponse<ProfileResponse>>


    @FormUrlEncoded
    @POST("patient_login")
    fun userLogin(@FieldMap params: Map<String, String>): Call<LoginApiResponse>


    @FormUrlEncoded
    @POST("get_compliance_report")
    fun get_compliance_report(@FieldMap params: Map<String, String>): Call<BaseResponse<CompliancereportDetails>>

    @FormUrlEncoded
    @POST("get_patient_health_detail")
    fun get_patient_health_detail(@FieldMap params: Map<String, String>): Call<BaseResponse<MedicalDetailsResponse>>

    @FormUrlEncoded
    @POST("getDateWiseSchedule")
    fun getDateWiseSchedule(@FieldMap params: Map<String, String>): Call<BaseResponse<DateWiseScheduleResponse>>

    @FormUrlEncoded
    @POST("add_medicine_schedule")
    fun add_medicine_schedule(@FieldMap params: Map<String, String>): Call<BaseResponse<CompliancereportDetails>>
   @FormUrlEncoded
    @POST("submit_patient_health_detail")
    fun submit_patient_health_detail(@FieldMap params: Map<String, String>): Call<BaseResponse<CompliancereportDetails>>

    @FormUrlEncoded
    @POST("get_upcoming_doses")
    fun get_upcoming_doses(@FieldMap params: Map<String, String>): Call<BaseResponse<CompliancereportDetails>>

    @FormUrlEncoded
    @POST("patient_product_schedule_details")
    fun patient_product_schedule_details(@FieldMap params: Map<String, String>): Call<PatientDetailsData>

    @FormUrlEncoded
    @POST("get_document_list")
    fun get_document_list(@FieldMap params: Map<String, String>): Call<BaseResponse<DocumentsModel>>

    @FormUrlEncoded
    @POST("get_video_list")
    fun get_video_list(@FieldMap params: Map<String, String>): Call<BaseResponse<MediaModel>>

    @FormUrlEncoded
    @POST("get_missed_doses")
    fun get_missed_doses(@FieldMap params: Map<String, String>): Call<BaseResponse<MissingdatesResponse>>

    @FormUrlEncoded
    @POST("get_missed_dose_details")
    fun get_missed_dose_details(@FieldMap params: Map<String, String>): Call<BaseResponse<MissdosedetailsResponse>>

    @FormUrlEncoded
    @POST("update_medicine_status")
    fun update_medicine_status(@FieldMap params: Map<String, String>): Call<SimpleResponse>

    @FormUrlEncoded
    @POST("patient_product_list")
    fun getMedicineList(@FieldMap params: Map<String, String>): Call<BaseTargetResponse<List<MedicineModel>>>

    @FormUrlEncoded
    @POST("getPatientInvoiceHistory")
    fun getPatientInvoiceHistory(@FieldMap params: Map<String, String>): Call<BaseTargetResponse<List<PurchasedHistoryResponse>>>


}
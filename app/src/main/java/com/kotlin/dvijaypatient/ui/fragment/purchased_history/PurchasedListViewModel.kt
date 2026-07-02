package com.kotlin.dvijaypatient.ui.fragment.purchased_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.dvijaypatient.model.BaseTargetResponse
import com.kotlin.dvijaypatient.model.MedicineModel
import com.kotlin.dvijaypatient.model.PurchasedHistoryResponse
import com.kotlin.dvijaypatient.network.Repository
import com.kotlin.dvijaypatient.network.RetrofitInstance


class PurchasedListViewModel : ViewModel() {
    private val repository = Repository(RetrofitInstance.api)

    private val _attendance_data = MutableLiveData<BaseTargetResponse<List<PurchasedHistoryResponse>>>()
    val attendance_data: LiveData<BaseTargetResponse<List<PurchasedHistoryResponse>>> get() = _attendance_data

    fun getAttendance(params: Map<String, String>) {
        repository.getPatientInvoiceHistory(params) {
            _attendance_data.postValue(it)
        }

    }



}
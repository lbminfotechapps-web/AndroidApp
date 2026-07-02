package com.kotlin.dvijaypatient.ui.fragment.medicine_schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.dvijaypatient.model.BaseTargetResponse
import com.kotlin.dvijaypatient.model.MedicineModel
import com.kotlin.dvijaypatient.network.Repository
import com.kotlin.dvijaypatient.network.RetrofitInstance


class MedicineListViewModel : ViewModel() {
    private val repository = Repository(RetrofitInstance.api)

    private val _attendance_data = MutableLiveData<BaseTargetResponse<List<MedicineModel>>>()
    val attendance_data: LiveData<BaseTargetResponse<List<MedicineModel>>> get() = _attendance_data

    fun getAttendance(params: Map<String, String>) {
        repository.patient_product_list(params) {
            _attendance_data.postValue(it)
        }

    }



}
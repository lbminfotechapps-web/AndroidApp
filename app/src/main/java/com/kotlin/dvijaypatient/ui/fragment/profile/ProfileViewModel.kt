package com.kotlin.dvijaypatient.ui.fragment.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.kotlin.dvijaypatient.model.BaseTargetResponse
import com.kotlin.dvijaypatient.model.ProfileResponse
import com.kotlin.dvijaypatient.network.Repository
import com.kotlin.dvijaypatient.network.RetrofitInstance


class ProfileViewModel : ViewModel() {
    private val repository = Repository(RetrofitInstance.api)

    private val _profile_data = MutableLiveData<BaseTargetResponse<ProfileResponse>>()
    val profile_data: LiveData<BaseTargetResponse<ProfileResponse>> get() = _profile_data

    fun getAttendance(params: Map<String, String>) {
        repository.getProfile(params) {
            _profile_data.postValue(it)
        }

    }



}
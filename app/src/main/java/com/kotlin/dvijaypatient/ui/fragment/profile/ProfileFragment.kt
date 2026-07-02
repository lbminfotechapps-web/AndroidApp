package com.kotlin.dvijaypatient.ui.fragment.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.databinding.FragmentMedicineScheduleBinding
import com.kotlin.dvijaypatient.databinding.FragmentProfileBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.MedicineModel

import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import com.kotlin.dvijaypatient.utils.MyLocationHelper
import okhttp3.MultipartBody

import java.io.File

class ProfileFragment : BaseFragment() {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()
    private var cd: ClassConnectionDetector? = null
    var user_id : String=""



    companion object {
        const val CAMERA_CAPTURE_IMAGE_REQUEST_CODE_FOR_INPUNCH = 4
        var imageStoragePath: String = ""
        private var file_extension: String? = null
        var uploadImage: MultipartBody.Part? = null
        var strprint: String = ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
       init()
    }


    private fun init(){
        val prefs = requireActivity()!!.getSharedPreferences(
            ClassGlobal.PREFERENCES,
            Context.MODE_PRIVATE
        )
        cd = ClassConnectionDetector(requireContext()!!)
        user_id = prefs.getString("user_id", "")!!
        Log.e("user_id4",user_id)
     //   initTitleBar("Profile",0)
        if (cd!!.isConnectingToInternet){
            getProfileDate()
        }


    }


    private fun getProfileDate() {
        showBaseProgressDialog()

        viewModel.profile_data.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                Log.e("getOrderHistory1",it.status.toString())
                hideBaseProgressDialog()
                if (it.status) {

                    Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()
                    binding.tvMobile1.setText(response.result.mobile)
                    binding.tvEmail.setText(response.result.email)
                    binding.tvGender.setText(response.result.gender)
                    binding.tvDob.setText(response.result.birth_date)
                    binding.tvMobile2.setText(response.result.mobile2)
                    binding.tvParentMobile.setText(response.result.parent_mobile)
                    binding.tvAddress.setText(response.result.address)
                    binding.tvCity.setText(response.result.city)
                    binding.tvDistrict.setText(response.result.district)
                    binding.tvState.setText(response.result.state)
                    binding.tvDoctor.setText(response.result.ref_doctor)
                    binding.tvBranch.setText(response.result.branch_id)
                    initTitleBar(response.result.patient_name,0)
                    hideBaseProgressDialog()

                } else {
                    hideBaseProgressDialog()

                     Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()


                }
            }
        })

        val params = HashMap<String, String>()
        params["patient_id"] = user_id
               for ((key, value) in params) {
            Log.e("TAG", "Key: $key:$value")
        }
        viewModel.getAttendance(params)
    }
}

package com.kotlin.dvijaypatient.ui.fragment.medicine_schedule

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.MedicineListAdapter
import com.kotlin.dvijaypatient.databinding.FragmentMedicineScheduleBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.MedicineModel

import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import okhttp3.MultipartBody

import java.io.File

class MedicineScheduleFragment : BaseFragment(), MedicineListAdapter.OnStatusClickListener{

    private lateinit var binding: FragmentMedicineScheduleBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cd: ClassConnectionDetector? = null
    private var user_id: String = ""
    private var loginUserDesg: String = ""
    private val viewModel: MedicineListViewModel by viewModels()
    private var attendanceList :MutableList<MedicineModel> = mutableListOf()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_medicine_schedule, container, false)
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {
      //  initTitleBar("Medicine Wise Schedule", 0)


        val prefs =
            requireActivity().getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        user_id = prefs.getString("user_id", "") ?: ""


        cd = ClassConnectionDetector(requireContext())

        binding.swipeRefreshLayout.setOnRefreshListener {
            getMedicineList()

            // Stop refresh after data is loaded
            binding.swipeRefreshLayout.isRefreshing = false
        }
        getMedicineList()


        Log.e("loginUserDesg", loginUserDesg)



    }



    private fun getMedicineList() {
        showBaseProgressDialog()

        val adapter = MedicineListAdapter(requireContext(),this)
        binding.rvMedicine.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvMedicine.adapter = adapter

        viewModel.attendance_data.observe(viewLifecycleOwner, Observer { response ->
            response?.let {
                binding.rvMedicine.visibility=View.VISIBLE
                Log.e("getOrderHistory1",it.status.toString())
                hideBaseProgressDialog()
                if (it.status) {
                    binding.rvMedicine.visibility= View.VISIBLE
                   // binding.tvNoRecord.visibility=View.GONE
                    attendanceList.clear()
                    attendanceList.addAll(it.result)
                    Log.e("getOrderHistory2",it.result.toString())
                    adapter.submitList(attendanceList)
                    adapter.notifyDataSetChanged()
                    hideBaseProgressDialog()

                } else {
                    hideBaseProgressDialog()
                    binding.rvMedicine.visibility=View.GONE
                    Toast.makeText(requireContext(), "No Records Found", Toast.LENGTH_SHORT).show()
                  //  binding.imgRecordNotfound.visibility=View.VISIBLE

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

   

    override fun onStatusClick(
        item: MedicineModel,
        position: Int,
        product_name: String
    ) {

        val bundle = Bundle()

        val selectedDays = arrayListOf("Monday", "Wednesday", "Friday")

        bundle.putStringArrayList("days_list", selectedDays)
        bundle.putString("product_name", product_name)
        bundle.putString("fld_product_id", item.fld_product_id)
        bundle.putString("isEdit", item.fld_product_id)

        findNavController().navigate(
            R.id.action_nav_medicine_to_nav_reminder_new,
            bundle
        )
    }
}

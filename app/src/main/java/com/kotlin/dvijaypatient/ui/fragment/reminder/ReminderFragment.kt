package com.kotlin.dvijaypatient.ui.fragment.reminder

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
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.CompliancereportAdapter
import com.kotlin.dvijaypatient.adapter.MedicineListAdapter
import com.kotlin.dvijaypatient.adapter.TimeAdapter
import com.kotlin.dvijaypatient.databinding.FragmentMedicineScheduleBinding
import com.kotlin.dvijaypatient.databinding.FragmentReminderBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.MedicineModel
import com.kotlin.dvijaypatient.network.RetrofitInstance

import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import com.kotlin.dvijaypatient.ui.fragment.set_reminder.SetReminderFragment
import com.kotlin.dvijaypatient.ui.fragment.set_reminder.TimeDialog
import com.kotlin.dvijaypatient.ui.fragment.set_reminder.WeekDays
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ReminderFragment : BaseFragment() {

    private lateinit var binding: FragmentReminderBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cd: ClassConnectionDetector? = null
    private var user_id: String = ""
    var fld_product_id: String = ""
    private var loginUserDesg: String = ""
    var in_out_status: String = ""
    var product_id: String = ""
    private val timeList = mutableListOf<String>()
    private lateinit var adapter: TimeAdapter
    private val selectedDaysSet = mutableSetOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder, container, false)
        binding.lifecycleOwner = this
        val daysList = arguments?.getStringArrayList("days_list") ?: arrayListOf()
        val product_name = arguments?.getString("product_name")
         fld_product_id = arguments?.getString("fld_product_id").toString()

        binding.tvProductName.setText(product_name)
        Log.e("DaysList", daysList.toString())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    @SuppressLint("SimpleDateFormat")
    private fun init() {
        initTitleBar("Medicine Wise Schedule", 0)
        /*val attendanceItem =
            arguments?.getSerializable("attendance_list") as? AttendanceListResponse
        attendanceItem?.let {

        }*/
        Log.e("in_out_status", in_out_status)

        val prefs =
            requireActivity().getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        user_id = prefs.getString("user_id", "") ?: ""


        cd = ClassConnectionDetector(requireContext())

        Log.e("loginUserDesg", loginUserDesg)

        setupFrequencySelection()
        setupRecycler()


        binding.ivStartDate.setOnClickListener {

            val dialog = SetReminderFragment()

            dialog.setListener(object : SetReminderFragment.OnDateSelectedListener {
                override fun onDateSelected(date: String) {
                    binding.tvStartDate.text = date
                }
            })

            dialog.show(parentFragmentManager, "SetReminderDialog")
        }

        binding.ivEndDate.setOnClickListener {
            val dialog = SetReminderFragment()

            dialog.setListener(object : SetReminderFragment.OnDateSelectedListener {
                override fun onDateSelected(date: String) {
                    binding.tvEndDate.text = date
                }
            })

            dialog.show(parentFragmentManager, "SetReminderDialog")
        }

        binding.ivDays.setOnClickListener {

            val dialog = WeekDays()

            // 👉 pass previous selection
            dialog.setPreSelectedDays(selectedDaysSet)

            dialog.setListener(object : WeekDays.OnDaysSelectedListener {
                override fun onDaysSelected(days: String) {

                    Log.e("selectedday", days)
                    binding.tvDays.text = days

                    // 👉 save again for next time
                    selectedDaysSet.clear()
                    selectedDaysSet.addAll(days.split(", ").filter { it.isNotEmpty() })
                }
            })

            dialog.show(parentFragmentManager, "WeekDaysDialog")
        }
        binding.tvSetTime.setOnClickListener {

            val dialog = TimeDialog()

            dialog.setListener(object : TimeDialog.OnTimeSelectedListener {
                override fun onTimeSelected(time: String) {

                    if (!timeList.contains(time)) {
                        timeList.add(time)
                        adapter.notifyDataSetChanged()
                    }
                }
            })

            dialog.show(parentFragmentManager, "TimeDialog")
        }

        binding.btnSubmit.setOnClickListener {
            if(binding.imgOnce.visibility != View.VISIBLE && binding.imgWeekly.visibility != View.VISIBLE && binding.imgMonthly.visibility != View.VISIBLE)
            {
                Toast.makeText(requireContext(),"Please Select Frequency Of Medicine", Toast.LENGTH_SHORT).show()
            }
            else if(binding.imgWeekly.visibility == View.VISIBLE && binding.tvDays.text.equals(""))
            {
                Toast.makeText(requireContext(),"Please Select Days Off Week Of Medicine Schedule", Toast.LENGTH_SHORT).show()
            }
            else if(binding.tvStartDate.text.equals("") && binding.tvEndDate.text.equals("") )
            {
                Toast.makeText(requireContext(),"Please Select Start Date and End Date Of Medicine Schedule", Toast.LENGTH_SHORT).show()
            }
            else if(timeList.size==0)
            {
                Toast.makeText(requireContext(),"Please Select Medicine Taken Time", Toast.LENGTH_SHORT).show()
            }
            else
            {
                getSubmitSchedule()
            }

        }
    }

    private fun getSubmitSchedule()
    {

        showBaseProgressDialog()

        RetrofitInstance.api.add_medicine_schedule(params()).enqueue(object :
            Callback<BaseResponse<CompliancereportDetails>> {
            override fun onResponse(call: Call<BaseResponse<CompliancereportDetails>>, response: Response<BaseResponse<CompliancereportDetails>>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse?.status!!){

                        Toast.makeText(requireContext(), response.body()?.message ?: "", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.nav_home)

                    }else{
                        Toast.makeText(requireContext(), response.body()?.message ?: "", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<BaseResponse<CompliancereportDetails>>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })

    }

    private fun params(): HashMap<String, String> {
        var Type: String=""
        if(binding.imgOnce.visibility == View.VISIBLE)
        {
            Type="one_or_more"
        }
         if(binding.imgWeekly.visibility == View.VISIBLE)
        {
            Type="weekly"
        }
        if(binding.imgMonthly.visibility == View.VISIBLE)
        {
            Type="monthly"
        }
        val formattedDate: String
        val toDate: String
        val gson = Gson()
        val timesJson = gson.toJson(timeList)

        val map = HashMap<String, String>()
        map["patient_id"] = user_id
        map["product_id"] = fld_product_id
        map["type"] = Type

        var formattedFromDate = ""
        var formattedToDate = ""

        if (binding.tvStartDate.text.toString().isNotEmpty()
            && binding.tvEndDate.text.toString().isNotEmpty()
        ) {

            try {
                val inputFormat = SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH)
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

                val start = inputFormat.parse(binding.tvStartDate.text.toString())
                val end = inputFormat.parse(binding.tvEndDate.text.toString())

                formattedFromDate = start?.let { outputFormat.format(it) } ?: ""
                formattedToDate = end?.let { outputFormat.format(it) } ?: ""

            } catch (e: Exception) {
                e.printStackTrace()
            }


            map["from_date"] = formattedFromDate
            map["to_date"] = formattedToDate
            map["times"] = timesJson
            map["week_days"] = binding.tvDays.text.toString()

            for ((key, value) in map) {
                Log.e("TAG", "Key: $key:$value")
            }

        }
        return map
    }

    private fun setupRecycler() {
        println("timeList size :"+timeList.size)
        adapter = TimeAdapter(timeList) { position ->
            timeList.removeAt(position)
            adapter.notifyDataSetChanged()
        }

        binding.rvTimes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTimes.adapter = adapter
    }
    private fun setupFrequencySelection() {




        binding.imgOnce.visibility = View.VISIBLE
        fun resetSelection() {
            binding.imgOnce.visibility = View.GONE
            binding.imgWeekly.visibility = View.GONE
            binding.imgMonthly.visibility = View.GONE

            binding.layOnce.setBackgroundColor(Color.TRANSPARENT)
            binding.layWeekly.setBackgroundColor(Color.TRANSPARENT)
            binding.layMonthly.setBackgroundColor(Color.TRANSPARENT)
        }

        binding.layOnce.setOnClickListener {
            resetSelection()
            binding.imgOnce.visibility = View.VISIBLE
            binding.cardDays.visibility = View.GONE
            binding.layOnce.setBackgroundColor(Color.parseColor("#E8F0FE"))
        }

        binding.layWeekly.setOnClickListener {
            resetSelection()
            binding.imgWeekly.visibility = View.VISIBLE
            binding.cardDays.visibility = View.VISIBLE
            binding.layWeekly.setBackgroundColor(Color.parseColor("#E8F0FE"))
        }

        binding.layMonthly.setOnClickListener {
            resetSelection()
            binding.imgMonthly.visibility = View.VISIBLE
            binding.cardDays.visibility = View.GONE
            binding.layMonthly.setBackgroundColor(Color.parseColor("#E8F0FE"))
        }
    }









}

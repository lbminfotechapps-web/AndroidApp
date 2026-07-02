package com.kotlin.dvijaypatient.ui.fragment.medical_details

import android.app.DatePickerDialog
import com.kotlin.dvijaypatient.R
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.kotlin.dvijaypatient.adapter.BPAdapter
import com.kotlin.dvijaypatient.adapter.MedicalDetailsAdapter
import com.kotlin.dvijaypatient.adapter.WeightAdapter
import com.kotlin.dvijaypatient.databinding.FragmentMedicalDetailsBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.MedicalDetailsResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.ui.fragment.compliancereport.CompliancereportViewModel
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import com.kotlin.dvijaypatient.ui.fragment.set_reminder.SetReminderFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.collections.get

class MedicalDetailsFragment : BaseFragment() {

    private var _binding: FragmentMedicalDetailsBinding? = null
    private val binding get() = _binding!!
    private var fromDate = ""
    private var toDate = ""
    private var medicalResponse: MedicalDetailsResponse? = null

    private val viewModel: CompliancereportViewModel by viewModels()

    companion object {
        fun newInstance() = MedicalDetailsFragment()
    }
    private var isLoggedIn: Boolean = false
    private var user_id: String = ""
    private var user_name: String = ""
    private val ComplianceDosesDatailsList = ArrayList<MedicalDetailsResponse>()
    private lateinit var adapter: MedicalDetailsAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentMedicalDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUI()
    }

    private fun initUI() {

        val prefs = requireActivity()!!.getSharedPreferences(
            ClassGlobal.PREFERENCES,
            Context.MODE_PRIVATE
        )
        isLoggedIn = prefs.getBoolean("is_login", false)
        user_id = prefs.getString("user_id", "")!!
        user_name = prefs.getString("user_name", "")!!
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val currentDate = sdf.format(Date())

        fromDate = currentDate
        toDate = currentDate

        binding.tvFromDate.text = currentDate
        binding.tvToDate.text = currentDate
        /*if(HomeFragment.complianceType.equals("compliance"))
        {
            binding.tvReportTitle.setText("Compliance Report")
        }
        else
        {
            binding.tvReportTitle.setText("Missed Pills Report")
        }*/
       // binding.tvReportTitle.visibility= View.GONE
        binding.swipeRefreshLayout.setOnRefreshListener {
            get_patient_health_detail()

            // Stop refresh after data is loaded
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.fabAddMedical.setOnClickListener {

            when(binding.tabMedical.selectedTabPosition){

                0 -> {
                    openAddSugarDialog()
                }

                1 -> {
                    openBPDialog()
                }

                2 -> {
                    openWeightDialog()
                }

            }

        }

        /*binding.tvFromDate.setOnClickListener {

            val dialog = SetReminderFragment()
            val bundle = Bundle()
            bundle.putString("TYPE", "Start Date")

            dialog.arguments = bundle

            dialog.setListener(object : SetReminderFragment.OnDateSelectedListener {
                override fun onDateSelected(date: String) {
                    binding.tvFromDate.text = date
                }
            })

            dialog.show(parentFragmentManager, "SetReminderDialog")
        }

        binding.tvToDate.setOnClickListener
        {

            val dialog = SetReminderFragment()

            val bundle = Bundle()
            bundle.putString("TYPE", "End Date")

            dialog.arguments = bundle


            dialog.setListener(object : SetReminderFragment.OnDateSelectedListener {

                override fun onDateSelected(date: String) {


                    binding.tvToDate.text = date


                    // Call API after date selected

                    get_patient_health_detail()

                }

            })


            dialog.show(parentFragmentManager, "SetReminderDialog")

        }*/


        binding.layoutFromDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                    fromDate = sdf.format(selectedDate.time)

                    binding.tvFromDate.text = fromDate

                    // Clear To Date when From Date changes
                    toDate = ""
                    binding.tvToDate.text = ""

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )


            // Disable previous dates
            //    datePicker.datePicker.minDate = System.currentTimeMillis()

            datePicker.show()
        }



        binding.layoutToDate.setOnClickListener {

            if (fromDate.isEmpty()) {

                Toast.makeText(
                    requireContext(),
                    "Please select From Date first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }


            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    val sdf = SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                    )

                    toDate = sdf.format(selectedDate.time)

                    binding.tvToDate.text = toDate
                    get_patient_health_detail()

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )


            try {

                val sdf = SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                )

                val from = sdf.parse(fromDate)

                if (from != null) {
                    datePicker.datePicker.minDate = from.time
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }


            datePicker.show()

        }
        setTabClick()
       // setDefaultDate()
        get_patient_health_detail()

    }




    private fun openAddSugarDialog() {

        val dialog = Dialog(requireContext())

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_sugar_details, null)

        dialog.setContentView(view)


        val tabSugarType =
            view.findViewById<TabLayout>(R.id.tabSugarType)

        val etSugarValue =
            view.findViewById<EditText>(R.id.etSugarValue)

        val tvSugarInfo =
            view.findViewById<TextView>(R.id.tvSugarInfo)

        val tvDateTime =
            view.findViewById<TextView>(R.id.tvDateTime)

        val btnSave =
            view.findViewById<Button>(R.id.btnSave)

        val ivClose =
            view.findViewById<ImageView>(R.id.ivClose)


        ivClose.setOnClickListener {

            dialog.dismiss()

        }

        etSugarValue.requestFocus()

        var selectedSugarType = "FASTING"





        tabSugarType.addOnTabSelectedListener(

            object : TabLayout.OnTabSelectedListener {


                override fun onTabSelected(tab: TabLayout.Tab?) {


                    when(tab?.position){


                        0 -> {

                            selectedSugarType = "FASTING"

                            tvSugarInfo.text =
                                "ⓘ Fasting: Requires at least 8 hours of fast"

                        }


                        1 -> {

                            selectedSugarType = "AFTER MEAL"

                            tvSugarInfo.text =
                                "ⓘ After Meal: Check after 2 hours of food"

                        }


                        2 -> {

                            selectedSugarType = "RANDOM"

                            tvSugarInfo.text =
                                "ⓘ Random: Sugar checked anytime"

                        }

                    }

                }


                override fun onTabUnselected(tab: TabLayout.Tab?) {}

                override fun onTabReselected(tab: TabLayout.Tab?) {}

            }
        )



        // Current Date Time

        val dateTime = SimpleDateFormat(
            "dd-MM-yyyy | hh:mm a",
            Locale.getDefault()
        ).format(Date())


        tvDateTime.text = "📅 Today | ${dateTime}"




        btnSave.setOnClickListener {


            val value =
                etSugarValue.text.toString()



            if(value.isEmpty()){

                etSugarValue.error =
                    "Enter sugar value"

                return@setOnClickListener

            }



            val params = HashMap<String,String>()


            params["fld_date"] =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                ).format(Date())


            params["fld_time"] =
                SimpleDateFormat(
                    "hh:mm",
                    Locale.getDefault()
                ).format(Date())


            params["fld_sugar_type"] =
                selectedSugarType


            params["fld_value"] =
                etSugarValue.text.toString()



            submit_patient_health_detail(
                "SUGAR",
                params
            )

            // Call API here


            dialog.dismiss()

        }



        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )


        dialog.show()


        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }

    private fun openBPDialog() {

        val dialog = Dialog(requireContext())

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_bp_details, null)

        dialog.setContentView(view)


        val etSys =
            view.findViewById<EditText>(R.id.etSys)

        val etDia =
            view.findViewById<EditText>(R.id.etDia)

        val etPulse =
            view.findViewById<EditText>(R.id.etPulse)


        val tvDateTime =
            view.findViewById<TextView>(R.id.tvDateTime)


        val btnSave =
            view.findViewById<Button>(R.id.btnSave)


        val ivClose =
            view.findViewById<ImageView>(R.id.ivClose)



        // Close Dialog

        ivClose.setOnClickListener {

            dialog.dismiss()

        }



        // Current Date Time

        val date =
            SimpleDateFormat(
                "dd-MM/yyyy",
                Locale.getDefault()
            ).format(Date())


        val time =
            SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            ).format(Date())



        tvDateTime.text =
            "📅 Today | $time"




        btnSave.setOnClickListener {


            val sys =
                etSys.text.toString()


            val dia =
                etDia.text.toString()


            val pulse =
                etPulse.text.toString()



            if(sys.isEmpty()){

                etSys.error = "Enter SYS value"

                return@setOnClickListener
            }



            if(dia.isEmpty()){

                etDia.error = "Enter DIA value"

                return@setOnClickListener
            }



            if(pulse.isEmpty()){

                etPulse.error = "Enter Pulse value"

                return@setOnClickListener
            }




            val params = HashMap<String,String>()


            params["fld_date"] =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                ).format(Date())


            params["fld_time"] =
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
                ).format(Date())


            params["fld_value"] =
                etSys.text.toString()


            params["fld_dia_value"] =
                etDia.text.toString()


            params["fld_pulse_value"] =
                etPulse.text.toString()



            submit_patient_health_detail(
                "BP",
                params
            )







            // API CALL HERE



            dialog.dismiss()


        }




        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )


        dialog.show()


        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }

    private fun openWeightDialog() {


        val dialog = Dialog(requireContext())


        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_add_weight_details,null)


        dialog.setContentView(view)



        val etWeight =
            view.findViewById<EditText>(R.id.etWeightValue)


        val tvDateTime =
            view.findViewById<TextView>(R.id.tvDateTime)


        val btnSave =
            view.findViewById<Button>(R.id.btnSave)


        val ivClose =
            view.findViewById<ImageView>(R.id.ivClose)




        ivClose.setOnClickListener {

            dialog.dismiss()

        }



        val date =
            SimpleDateFormat(
                "dd-MM/yyyy",
                Locale.getDefault()
            ).format(Date())


        val time =
            SimpleDateFormat(
                "hh:mm a",
                Locale.getDefault()
            ).format(Date())



        tvDateTime.text =
            "📅 Today | $time"




        btnSave.setOnClickListener {


            val weight =
                etWeight.text.toString()



            if(weight.isEmpty()){

                etWeight.error="Enter weight"

                return@setOnClickListener

            }



            val params = HashMap<String,String>()


            params["fld_date"] =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                ).format(Date())


            params["fld_time"] =
                SimpleDateFormat(
                    "hh:mm a",
                    Locale.getDefault()
                ).format(Date())


            params["fld_value"] =
                etWeight.text.toString()



            submit_patient_health_detail(
                "WEIGHT",
                params
            )


            // API CALL


            dialog.dismiss()

        }



        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )


        dialog.show()



        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }
    private fun submit_patient_health_detail(
        type: String,
        extraParams: HashMap<String,String>
    ) {

        showBaseProgressDialog()


        val map = HashMap<String,String>()


        map["fld_patient_id"] = user_id

        map["fld_type"] = type


        map.putAll(extraParams)



        // Print params

        for ((key,value) in map){

            Log.e(
                "HEALTH_PARAM",
                "$key : $value"
            )

        }



        RetrofitInstance.api.submit_patient_health_detail(map)
            .enqueue(object :
                Callback<BaseResponse<CompliancereportDetails>> {


                override fun onResponse(
                    call: Call<BaseResponse<CompliancereportDetails>>,
                    response: Response<BaseResponse<CompliancereportDetails>>
                ) {


                    hideBaseProgressDialog()


                    if(response.isSuccessful){


                        val apiResponse = response.body()


                        if(apiResponse?.status == true){


                            Toast.makeText(
                                requireContext(),
                                apiResponse.message,
                                Toast.LENGTH_SHORT
                            ).show()



                            findNavController()
                                .navigate(R.id.nav_medical_details)


                        }else{


                            Toast.makeText(
                                requireContext(),
                                apiResponse?.message ?: "",
                                Toast.LENGTH_SHORT
                            ).show()

                        }


                    }


                }


                override fun onFailure(
                    call: Call<BaseResponse<CompliancereportDetails>>,
                    t: Throwable
                ) {

                    hideBaseProgressDialog()

                    ClassGlobal.showErrorDialog(
                        requireContext(),
                        t.message.toString(),
                        null
                    )

                }


            })

    }

    private fun get_patient_health_detail() {

        showBaseProgressDialog()


        RetrofitInstance.api.get_patient_health_detail(params())
            .enqueue(object :
                Callback<BaseResponse<MedicalDetailsResponse>> {


                override fun onResponse(
                    call: Call<BaseResponse<MedicalDetailsResponse>>,
                    response: Response<BaseResponse<MedicalDetailsResponse>>
                ) {


                    hideBaseProgressDialog()


                    if(response.isSuccessful){
                        binding.rvCompliance.visibility= View.VISIBLE

                        val apiResponse = response.body()


                        if(apiResponse?.status == true){

                            medicalResponse = apiResponse.result[0]


                            // Default Sugar

                            binding.rvCompliance.layoutManager =
                                LinearLayoutManager(requireContext())


                            binding.rvCompliance.adapter =
                                MedicalDetailsAdapter(
                                    medicalResponse!!.sugar
                                )

                            binding.tabMedical.selectTab(
                                binding.tabMedical.getTabAt(0)
                            )
                        }else{
                            binding.rvCompliance.visibility= View.VISIBLE
                            Toast.makeText(requireContext(), "No Records Found", Toast.LENGTH_SHORT).show()

                            /*binding.imgRecordNotfound.visibility =
                                View.VISIBLE*/
                        }


                    }

                }


                override fun onFailure(
                    call: Call<BaseResponse<MedicalDetailsResponse>>,
                    t: Throwable
                ) {

                    hideBaseProgressDialog()

                    Log.e("API_ERROR",t.message.toString())

                }

            })

    }
    private fun params(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["fld_patient_id"] = user_id
        map["fld_fromDate"] =
            binding.tvFromDate.text.toString().trim()


        map["fld_toDate"] =
          binding.tvToDate.text.toString().trim()
        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }

    private fun setTabClick(){


        binding.tabMedical.addOnTabSelectedListener(

            object : TabLayout.OnTabSelectedListener {


                override fun onTabSelected(tab: TabLayout.Tab?) {


                    Log.e("TAB_CLICK","Position : ${tab?.position}")


                    val data = medicalResponse ?: return



                    when(tab?.position){


                        0 -> {


                            binding.rvCompliance.adapter =
                                MedicalDetailsAdapter(
                                    data.sugar
                                )

                        }



                        1 -> {


                            binding.rvCompliance.adapter =
                                BPAdapter(
                                    data.bp
                                )

                        }



                        2 -> {


                            binding.rvCompliance.adapter =
                                WeightAdapter(
                                    data.weight
                                )

                        }

                    }


                }


                override fun onTabUnselected(tab: TabLayout.Tab?) {}


                override fun onTabReselected(tab: TabLayout.Tab?) {}



            })


    }

    private fun convertDateFormat(date:String):String{

        return try {

            val input =
                SimpleDateFormat(
                    "EEE dd MMM yyyy",
                    Locale.ENGLISH
                )


            val output =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.ENGLISH
                )


            val parseDate =
                input.parse(date)


            output.format(parseDate!!)


        }catch (e:Exception){

            ""

        }

    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   // 🔥 prevent memory leak
    }
}
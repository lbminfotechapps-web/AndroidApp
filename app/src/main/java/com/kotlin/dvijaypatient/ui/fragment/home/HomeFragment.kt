package com.kotlin.dvijaypatient.ui.fragment.home

import android.content.Context
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.DateAdapter
import com.kotlin.dvijaypatient.adapter.MedicineCheckAdapter
import com.kotlin.dvijaypatient.databinding.FragmentHomeBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.BaseTargetResponse
import com.kotlin.dvijaypatient.model.MissdosedetailsResponse
import com.kotlin.dvijaypatient.model.MissingdatesResponse
import com.kotlin.dvijaypatient.model.SimpleResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class HomeFragment : BaseFragment() {

    companion object {
        fun newInstance() = HomeFragment()
        var complianceType: String = ""
    }

    private lateinit var view: View
    private lateinit var binding: FragmentHomeBinding
    private var isLoggedIn: Boolean = false
    private var user_id: String = ""
    private var user_name: String = ""
    private var selected_Date: String = ""
    private var permissionDialog: AlertDialog? = null
    private val missingDosesDateList = ArrayList<MissingdatesResponse>()
    private val missingDosesDatailsList = ArrayList<MissdosedetailsResponse>()


    private val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val view = binding.root
        init()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Better place to request runtime permissions

    }

    private fun init() {
        val prefs = requireActivity()!!.getSharedPreferences(
            ClassGlobal.PREFERENCES,
            Context.MODE_PRIVATE
        )
        isLoggedIn = prefs.getBoolean("is_login", false)
        user_id = prefs.getString("user_id", "")!!
        user_name = prefs.getString("user_name", "")!!
        Log.e("user_id_list", user_id)
       // binding.tvUsername.setText(user_name)
        Log.e("Homecall","HomeCall")
        fragmnetNavigation()
        if (isLoggedIn){
            getMissingMedicalDosesDate()
        }

    }

    private fun getMissingMedicalDosesDate()
    {

        showBaseProgressDialog()

        RetrofitInstance.api.get_missed_doses(params()).enqueue(object :
            Callback<BaseResponse<MissingdatesResponse>> {
            override fun onResponse(call: Call<BaseResponse<MissingdatesResponse>>, response: Response<BaseResponse<MissingdatesResponse>>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                    Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (apiResponse?.status!!){

                       // context?.toast(response.body()!!.message)
                        missingDosesDateList.clear()
                        missingDosesDateList.addAll(response.body()!!.result)

                        showDateDialog(missingDosesDateList)
                    }else{
                       // context?.toast(response.body()!!.message)
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<BaseResponse<MissingdatesResponse>>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })

    }

    private fun params(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["patient_id"] = user_id
        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }

    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()



    private fun showDateDialog(missingDosesDateList: ArrayList<MissingdatesResponse>) {

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_date_list, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val rvDates = view.findViewById<RecyclerView>(R.id.rvDates)
        val imgClose = view.findViewById<ImageView>(R.id.imgClose)


        rvDates.layoutManager = LinearLayoutManager(requireContext())
        rvDates.adapter = DateAdapter(missingDosesDateList) { selectedDate ->

            dialog.dismiss()
            selected_Date=selectedDate;
            getMissingMedicalDosesDetails(selectedDate)

        }

        imgClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun showMedicineDialog(
        selectedDate: String,
        missingDosesDatailsList: ArrayList<MissdosedetailsResponse>
    ) {

        val view = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_medicine_check, null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val rv = view.findViewById<RecyclerView>(R.id.rvMedicineCheck)
        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val btnCancel = view.findViewById<Button>(R.id.btnCancel)

        tvDate.text = "Missing Dose Date : "+formatDate(selectedDate)   // 🔥 show selected date

        val adapter = MedicineCheckAdapter(missingDosesDatailsList,
            object : MedicineCheckAdapter.OnMedicineCheckListener
            {
                override fun onMedicineChecked(item: MissdosedetailsResponse)
                {
                    dialog.dismiss()
                    MedicineStatusUpdateApi(item)
                }
            }
        )
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = adapter

        btnSubmit.setOnClickListener {

            missingDosesDatailsList.forEach {
                Log.d("DATA", "$selectedDate -> ${it.fld_prod_name} = ${it.fld_schedule_date}")
            }

            dialog.dismiss()
        }

        btnCancel.setOnClickListener {


            dialog.dismiss()
        }

        dialog.show()
    }
    fun formatDate(inputDate: String): String {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }

    private fun getMissingMedicalDosesDetails(selectedDate: String)
    {

        showBaseProgressDialog()

        RetrofitInstance.api.get_missed_dose_details(param()).enqueue(object :
            Callback<BaseResponse<MissdosedetailsResponse>> {
            override fun onResponse(call: Call<BaseResponse<MissdosedetailsResponse>>, response: Response<BaseResponse<MissdosedetailsResponse>>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                    Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (apiResponse?.status!!){

                       // context?.toast(response.body()!!.message)
                        missingDosesDatailsList.clear()
                        missingDosesDatailsList.addAll(response.body()!!.result)

                        showMedicineDialog(selectedDate,missingDosesDatailsList)
                    }else{
                       // context?.toast(response.body()!!.message)
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<BaseResponse<MissdosedetailsResponse>>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })

    }

    private fun param(): HashMap<String, String> {
        val map = HashMap<String, String>()
        map["patient_id"] = user_id
        map["schedule_date"] = selected_Date

        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }
    private fun MedicineStatusUpdateApi(item: MissdosedetailsResponse) {

        showBaseProgressDialog()
        var status: String=""
        if(item.isTaken.equals("Yes"))
        {
            status="1"
        }
        else if(item.isTaken.equals("No"))
        {
            status="0"
        }

        val map = HashMap<String, String>()
        map["schedule_id"] = item.fld_schedule_id
        map["status"] =status

        println("schedule_id:"+item.fld_schedule_id)
        println("status:"+status)
        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        RetrofitInstance.api.update_medicine_status(map)
            .enqueue(object : Callback<SimpleResponse> {

                override fun onResponse(
                    call: Call<SimpleResponse>,
                    response: Response<SimpleResponse>
                ) {
                    hideBaseProgressDialog()

                    if (response.isSuccessful && response.body() != null) {

                        val apiResponse = response.body()!!

                        Log.d("API_RESPONSE", apiResponse.toString())

                        if (apiResponse.status) {   // ✅ FIXED HERE
                            context?.toast(apiResponse.message)
                            getMissingMedicalDosesDetails(selected_Date)
                        } else {
                            context?.toast(apiResponse.message)
                            getMissingMedicalDosesDetails(selected_Date)
                        }

                    } else {
                        context?.toast("Something went wrong")
                    }
                }

                override fun onFailure(call: Call<SimpleResponse>, t: Throwable) {
                    hideBaseProgressDialog()
                    Log.d("API_ERROR", t.message.toString())
                    ClassGlobal.showErrorDialog(requireContext(), t.message.toString(), null)
                }
            })
    }



    override fun onResume() {
        super.onResume()
        Handler(Looper.getMainLooper()).postDelayed({

        }, 800)
        initTitleBar(user_name, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        permissionDialog?.dismiss()
        permissionDialog = null
    }

    private fun fragmnetNavigation() {
        binding.cardMedicineList.setOnClickListener {
            findNavController().navigate(R.id.action_nav_home_to_nav_medicine)
        }

        binding.cvComplianceReport.setOnClickListener {
            complianceType="compliance"
            findNavController().navigate(R.id.action_nav_home_to_nav_compliance)
        }

        binding.cvMissingpillsReport.setOnClickListener {
            complianceType="missed"
            findNavController().navigate(R.id.action_nav_home_to_nav_compliance)
        }

        binding.cardPillReminderList.setOnClickListener{
            complianceType="reminder"
            findNavController().navigate(R.id.action_nav_home_to_nav_pill_reminder)
        }

        binding.cvGallery.setOnClickListener{
            findNavController().navigate(R.id.action_nav_home_to_nav_gallery)
        }

        binding.cvInvoiceHistory.setOnClickListener{
            findNavController().navigate(R.id.action_nav_home_to_nav_purchased_history)
        }

        binding.cvCalendar.setOnClickListener{
            findNavController().navigate(R.id.action_nav_home_to_nav_calendar)
        }

        binding.cvMedicalDetails.setOnClickListener{
            findNavController().navigate(R.id.action_nav_home_to_nav_medical_details)
        }
    }


}
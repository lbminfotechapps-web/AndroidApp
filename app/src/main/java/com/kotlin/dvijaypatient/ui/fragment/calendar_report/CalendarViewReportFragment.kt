package com.kotlin.dvijaypatient.ui.fragment.calendar_report

import android.content.Context
import android.graphics.Color
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.postDelayed
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.CalendarDateAdapter
import com.kotlin.dvijaypatient.adapter.DateWiseScheduleAdapter
import com.kotlin.dvijaypatient.databinding.FragmentCalendaerviewBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.DateModel
import com.kotlin.dvijaypatient.model.DateWiseScheduleResponse
import com.kotlin.dvijaypatient.model.MissdosedetailsResponse
import com.kotlin.dvijaypatient.model.SimpleResponse
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.logging.Handler

class CalendarViewReportFragment : BaseFragment(), DateWiseScheduleAdapter.OnScheduleClickListener {

    private var _binding: FragmentCalendaerviewBinding? = null
    private val binding get() = _binding!!
    private var selected_Date: String = ""
    private lateinit var calendarAdapter: CalendarDateAdapter
    private lateinit var dateLayoutManager: LinearLayoutManager

    private val viewModel: CalendarViewModel by viewModels()

    companion object {
        fun newInstance() = CalendarViewReportFragment()
    }
    private var isLoggedIn: Boolean = false
    private var user_id: String = ""
    private var user_name: String = ""
    private val ComplianceDosesDatailsList = ArrayList<DateWiseScheduleResponse>()
    private lateinit var adapter: DateWiseScheduleAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        _binding = FragmentCalendaerviewBinding.inflate(inflater, container, false)
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

        if(HomeFragment.complianceType.equals("compliance"))
        {
            binding.tvReportTitle.setText("Compliance Report")
        }
        else
        {
            binding.tvReportTitle.setText("Missed Pills Report")
        }

        binding.tvReportTitle.visibility= View.GONE
        binding.swipeRefreshLayout.setOnRefreshListener {
            setDateRecycler()

            // Stop refresh after data is loaded
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.imgCalendar.setOnClickListener {
            openCalendar()
        }

        setDateRecycler()
    }

    private fun openCalendar() {


        val datePicker =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .setSelection(
                    MaterialDatePicker.todayInUtcMilliseconds()
                )
                .build()



        datePicker.addOnPositiveButtonClickListener { selectedDate ->



            val calendar = Calendar.getInstance()

            calendar.timeInMillis = selectedDate



            val selectedDay =
                calendar.get(Calendar.DAY_OF_MONTH)



            val selectedMonth =
                calendar.get(Calendar.MONTH)



            val selectedYear =
                calendar.get(Calendar.YEAR)



            // Format API date

            val selected =
                SimpleDateFormat(
                    "dd-MM-yyyy",
                    Locale.getDefault()
                )
                    .format(calendar.time)



            Log.e(
                "SELECTED_DATE",
                selected
            )



            // API CALL

            getDateWiseSchedule(selected)




            // Update header

            binding.tvToday.text =
                SimpleDateFormat(
                    "EEE, MMM dd",
                    Locale.getDefault()
                )
                    .format(calendar.time)




            // Find selected date in RecyclerView

            val position =
                calendarAdapter.findDatePosition(
                    selectedDay,
                    selectedMonth,
                    selectedYear
                )



            if(position >= 0){



                // Update blue selected date

                calendarAdapter.setSelectedDate(position)



                // Scroll selected date center

                binding.rvDate.post {


                    dateLayoutManager.scrollToPositionWithOffset(

                        position,

                        binding.rvDate.width / 2

                    )


                }


            }



        }



        datePicker.show(
            childFragmentManager,
            "DATE_PICKER"
        )

    }

    private fun setDateRecycler() {


        val calendar = Calendar.getInstance()


        val startYear = calendar.get(Calendar.YEAR)

        val startMonth = calendar.get(Calendar.MONTH)



        val days = mutableListOf<DateModel>()



        val temp = Calendar.getInstance()


        temp.set(
            startYear,
            startMonth,
            1
        )



        // Generate all dates (1 year)

        repeat(365) {


            val day =
                SimpleDateFormat(
                    "EEE",
                    Locale.getDefault()
                )
                    .format(temp.time)



            days.add(

                DateModel(

                    day,

                    temp.get(Calendar.DAY_OF_MONTH)
                        .toString(),

                    temp.get(Calendar.MONTH),

                    temp.get(Calendar.YEAR)

                )

            )



            temp.add(
                Calendar.DATE,
                1
            )


        }




        dateLayoutManager =
            LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )



        binding.rvDate.layoutManager =
            dateLayoutManager





        calendarAdapter =
            CalendarDateAdapter(days) { item, position ->



                val selectedCalendar =
                    Calendar.getInstance()



                selectedCalendar.set(

                    item.year,

                    item.month,

                    item.date.toInt()

                )




                val apiDate =
                    SimpleDateFormat(
                        "dd-MM-yyyy",
                        Locale.getDefault()
                    )
                        .format(selectedCalendar.time)




                // Update Header

                binding.tvToday.text =
                    SimpleDateFormat(
                        "EEE, MMM dd",
                        Locale.getDefault()
                    )
                        .format(selectedCalendar.time)





                Log.e(
                    "SELECTED_DATE",
                    apiDate
                )




                // API CALL
                selected_Date=apiDate
                getDateWiseSchedule(selected_Date)




            }





        binding.rvDate.adapter =
            calendarAdapter






        // ==========================
        // CURRENT DATE SELECTION
        // ==========================


        val today =
            Calendar.getInstance()




        val todayPosition =
            days.indexOfFirst {



                it.date ==
                        today.get(Calendar.DAY_OF_MONTH)
                            .toString()


                        &&


                        it.month ==
                        today.get(Calendar.MONTH)


                        &&


                        it.year ==
                        today.get(Calendar.YEAR)



            }





        if(todayPosition >= 0){


            // select today's date

            calendarAdapter.setSelectedDate(
                todayPosition
            )



            // scroll today

            binding.rvDate.post {


                dateLayoutManager.scrollToPositionWithOffset(

                    todayPosition,

                    binding.rvDate.width / 2

                )

            }



        }





        // ==========================
        // API CALL ON SCREEN OPEN
        // ==========================


        val todayApiDate =
            SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.getDefault()
            )
                .format(today.time)




        binding.tvToday.text =
            SimpleDateFormat(
                "EEE, MMM dd",
                Locale.getDefault()
            )
                .format(today.time)





        Log.e(
            "TODAY_API_DATE",
            todayApiDate
        )


        selected_Date=todayApiDate

        getDateWiseSchedule(todayApiDate)



    }

    private fun initRecycler() {

        adapter = DateWiseScheduleAdapter(
            ComplianceDosesDatailsList,this
        )

        binding.rvCompliance.layoutManager =
            LinearLayoutManager(requireContext())

        binding.rvCompliance.adapter = adapter

    }
    private fun getDateWiseSchedule(date: String)
    {
        val map = HashMap<String, String>()
        map["patient_id"] = user_id
        map["schedule_date"] = date
        /*map["from_date"] = "2026-04-01"*/
        /*map["to_date"] = "2026-04-30"*/
        // map["product_name"] = ""
        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }


        showBaseProgressDialog()



        RetrofitInstance.api.getDateWiseSchedule(map).enqueue(object :
            Callback<BaseResponse<DateWiseScheduleResponse>> {
            override fun onResponse(call: Call<BaseResponse<DateWiseScheduleResponse>>, response: Response<BaseResponse<DateWiseScheduleResponse>>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                    Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (apiResponse.status) {
                       // binding.imgRecordNotfound.visibility=View.GONE
                        binding.rvCompliance.visibility= View.VISIBLE
                        ComplianceDosesDatailsList.clear()


                        ComplianceDosesDatailsList.addAll(
                            apiResponse.result
                        )


                        Log.e(
                            "listSize",
                            ComplianceDosesDatailsList.size.toString()
                        )
                        initRecycler()

                        adapter.notifyDataSetChanged()


                    }else{
                        context?.toast(response.body()!!.message)
                        binding.rvCompliance.visibility= View.GONE
                       // binding.imgRecordNotfound.visibility=View.VISIBLE
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<BaseResponse<DateWiseScheduleResponse>>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })

    }


    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null   // 🔥 prevent memory leak
    }

    override fun onScheduleClick(
        item: DateWiseScheduleResponse,
        position: Int
    ) {


        showStatusBottomSheet(
            item,
            position
        )

    }
 /*   private fun updateMedicineStatus(
        item: DateWiseScheduleResponse,
        position: Int,
        status:String
    ){


        val params = HashMap<String,String>()


        params["schedule_id"] =
            item.fld_id


        params["status"] =
            status



        RetrofitInstance.api
            .updateStatus(params)
            .enqueue(object: Callback<BaseResponse<Any>>{


                override fun onResponse(
                    call: Call<BaseResponse<Any>>,
                    response: Response<BaseResponse<Any>>
                ){


                    if(response.isSuccessful){


                        item.fld_status = status


                        adapter.notifyItemChanged(position)


                    }


                }



                override fun onFailure(
                    call: Call<BaseResponse<Any>>,
                    t: Throwable
                ){

                }


            })


    }*/

    private fun showStatusBottomSheet(
        item: DateWiseScheduleResponse,
        position: Int
    ) {


        val dialog =
            BottomSheetDialog(requireContext())


        val view =
            layoutInflater.inflate(
                R.layout.bottomsheet_update_status,
                null
            )


        dialog.setContentView(view)



        val llTaken =
            view.findViewById<LinearLayout>(R.id.llTaken)

        val llSkip =
            view.findViewById<LinearLayout>(R.id.llSkip)

        val llNoStatus =
            view.findViewById<LinearLayout>(R.id.llNoStatus)



        // selected background

        fun clearSelection(){

            llTaken.setBackgroundResource(
                android.R.color.transparent
            )

            llSkip.setBackgroundResource(
                android.R.color.transparent
            )

            llNoStatus.setBackgroundResource(
                android.R.color.transparent
            )

        }



        fun setSelected(view: LinearLayout){

            clearSelection()


            view.setBackgroundResource(
                R.drawable.bg_selected_status
            )


        }




        llTaken.setOnClickListener {


            setSelected(llTaken)

            //getDateWiseSchedule()

            MedicineStatusUpdateApi(
                item,
                "1"
            )


            android.os.Handler(Looper.getMainLooper()).postDelayed({

                dialog.dismiss()

            },300)


        }





        llSkip.setOnClickListener {


            setSelected(llSkip)



            MedicineStatusUpdateApi(
                item,
                "0"
            )



            android.os.Handler(Looper.getMainLooper()).postDelayed({

                dialog.dismiss()

            },300)


        }





        llNoStatus.setOnClickListener {


            setSelected(llNoStatus)



            MedicineStatusUpdateApi(
                item,
                "2"
            )



            android.os.Handler(Looper.getMainLooper()).postDelayed({

                dialog.dismiss()

            },300)


        }



        dialog.show()


    }


    private fun MedicineStatusUpdateApi(
        item: DateWiseScheduleResponse,
        status: String
    ) {


        showBaseProgressDialog()


        val map = HashMap<String,String>()


        map["schedule_id"] =
            item.fld_schedule_id


        map["status"] =
            status



        Log.e(
            "schedule_id",
            item.fld_schedule_id
        )


        Log.e(
            "status",
            status
        )



        RetrofitInstance.api.update_medicine_status(map)
            .enqueue(object : Callback<SimpleResponse>{



                override fun onResponse(
                    call: Call<SimpleResponse>,
                    response: Response<SimpleResponse>
                ) {


                    hideBaseProgressDialog()



                    if(response.isSuccessful &&
                        response.body()!=null){


                        val apiResponse =
                            response.body()!!


                        if(apiResponse.status){


                            context?.toast(
                                apiResponse.message
                            )


                            // reload list

                            getDateWiseSchedule(selected_Date)


                        }
                        else{


                            context?.toast(
                                apiResponse.message
                            )

                        }


                    }


                }



                override fun onFailure(
                    call: Call<SimpleResponse>,
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
}
package com.kotlin.dvijaypatient.ui.fragment.reminder_new
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.CompliancereportAdapter
import com.kotlin.dvijaypatient.databinding.FragmentReminderNewBinding
import com.kotlin.dvijaypatient.databinding.SpinnerDropdownBlueBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.BaseResponse
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.PatientDetailsData
import com.kotlin.dvijaypatient.network.RetrofitInstance
import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.ui.fragment.set_reminder.SetReminderFragment
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ReminderNewFragment : BaseFragment() {

    private lateinit var binding: FragmentReminderNewBinding
    private var intervalType = ""
    private var fromDate = ""
    private var toDate = ""
    var startDate = ""
    private var intervalValue = ""
    private var formattedFromDate = ""
    private var formattedToDate = ""
    private var timesPerDay = ""

    private var selectedFrequencyType = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var cd: ClassConnectionDetector? = null
    private var user_id: String = ""
    private var apiType = ""
    var fld_product_id: String = ""
    private var loginUserDesg: String = ""
    var in_out_status: String = ""
    var product_id: String = ""
    private var timeList = mutableListOf<String>()
    private val selectedDays = mutableListOf<String>()
    private var weekDays = ""



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reminder_new, container, false)
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
    private fun init()
    {
        val prefs =
            requireActivity().getSharedPreferences(ClassGlobal.PREFERENCES, Context.MODE_PRIVATE)
        user_id = prefs.getString("user_id", "") ?: ""


        /*binding.llStartDate.setOnClickListener {

            val dialog = SetReminderFragment()

            val bundle = Bundle()
            bundle.putString("TYPE", "Start Date")

            dialog.arguments = bundle


            dialog.setListener(object : SetReminderFragment.OnDateSelectedListener {

                override fun onDateSelected(date: String) {

                    startDate = date

                    binding.tvStartDate.text = date

                    // clear end date
                    binding.tvEndDate.text = ""
                }

            })


            dialog.show(parentFragmentManager,"SetReminderDialog")
        }*/

        binding.llStartDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->

                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(year, month, dayOfMonth)

                    val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

                    fromDate = sdf.format(selectedDate.time)

                    binding.tvStartDate.text = fromDate

                    // Clear To Date when From Date changes
                    toDate = ""
                    binding.tvEndDate.text = ""

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )


            // Disable previous dates
            //    datePicker.datePicker.minDate = System.currentTimeMillis()

            datePicker.show()
        }



        binding.llEndtDate.setOnClickListener {

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

                    binding.tvEndDate.text = toDate
                    //getPurchasedHistory()

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


        binding.tvPillsCnt.setOnClickListener {

            binding.tvPillsCnt.requestFocus()

            binding.tvPillsCnt.selectAll()

        }

        /*binding.llEndtDate.setOnClickListener {


            if(startDate.isEmpty()){

                Toast.makeText(
                    requireContext(),
                    "Please select Start Date first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }



            val dialog = SetReminderFragment()


            val bundle = Bundle()

            bundle.putString("TYPE","End Date")

            bundle.putString(
                "MIN_DATE",
                startDate
            )


            dialog.arguments = bundle



            dialog.setListener(object : SetReminderFragment.OnDateSelectedListener {

                override fun onDateSelected(date: String) {

                    binding.tvEndDate.text = date

                }

            })


            dialog.show(
                parentFragmentManager,
                "SetReminderDialog"
            )


        }*/

        binding.btnDone.setOnClickListener {

            if (binding.tvStartDate.text.isNullOrEmpty()) {
                context?.toast("Please select start date")
                return@setOnClickListener
            }

            if (timeList.isEmpty()) {
                context?.toast("Please select at least one time")
                return@setOnClickListener
            }

            getSubmitSchedule()
        }

        /* binding.tvTime.setOnClickListener {

             openTimePicker(binding.tvTime)

         }*/
        patient_product_schedule_details()
        setFrequency()
        setDose()
    }
    private fun patient_product_schedule_details()
    {

        showBaseProgressDialog()

        RetrofitInstance.api.patient_product_schedule_details(params_details()).enqueue(object :
            Callback<PatientDetailsData> {
            override fun onResponse(call: Call<PatientDetailsData>, response: Response<PatientDetailsData>) {
                hideBaseProgressDialog()
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    Log.d("API_RESPONSE", apiResponse.toString())
                   // Log.d("API_RESPONSE1", apiResponse!!.status.toString())

                    if (response.body()?.status == true) {

                        context?.toast(response.body()!!.message)

                        binding.btnDone.text = "Update"


                        val startDate = response.body()!!.start_date
                        val endDate = response.body()!!.end_date


                        binding.tvStartDate.text = formatDate(startDate)
                        binding.tvEndDate.text = formatDate(endDate)


                        binding.tvPillsCnt.setText(response.body()!!.pills_count)


                        val doseUnit = response.body()!!.dose_unit
                        val type = response.body()!!.type
                         apiType = response.body()!!.type

                        binding.tvDose.setText(doseUnit)   // <-- set selected dose here
                        binding.tvFrequency.setText(type)   // <-- set selected dose here
                        timesPerDay=response.body()!!.times_per_day
                        val scheduleTime = response.body()!!.schedule_time
                        timeList.clear()
                        timeList.addAll(scheduleTime)
                        binding.tvTime.text = scheduleTime.joinToString(", ")


                    }
                    else {


                        val todayDate = getCurrentDate()

                        binding.tvStartDate.text = todayDate
                        binding.tvEndDate.text = todayDate

                        binding.btnDone.text = "Done"

                        context?.toast(response.body()!!.message)
                    }
                } else {
                    context?.toast("Login Successful")
                }
            }

            override fun onFailure(call: Call<PatientDetailsData>, t: Throwable) {
                Log.d("API_RESPONSE", "API call failed: ${t.message}")
                hideBaseProgressDialog()
                ClassGlobal.showErrorDialog(requireContext(),t.message.toString(),null)
            }
        })
    }
    private fun formatDate(date: String): String {

        return try {

            val inputFormat = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.ENGLISH
            )

            val outputFormat = SimpleDateFormat(
                "dd-MM-yyyy",
                Locale.ENGLISH
            )

            val parsedDate = inputFormat.parse(date)

            outputFormat.format(parsedDate!!)

        } catch (e: Exception) {

            e.printStackTrace()
            date
        }

    }



    private fun getCurrentDate(): String {

        val sdf = SimpleDateFormat(
            "dd-MM-yy",
            Locale.getDefault()
        )

        return sdf.format(Date())
    }
    fun Context.toast(message: CharSequence) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


    private fun setDose() {

        val doseUnits = listOf(
            "Tablet",
            "Capsule",
            "Teaspoon",
            "Tablespoon",
            "Pump",
            "ML",
            "Suppository",
            "Puff",
            "Ampoule",
            "Packet",
            "Pessary",
            "Piece",
            "Fingertip",
            "Device",
            "Application",
            "Spray",
            "Drop",
            "Unit",
            "Injection",
            "Scoop",
            "Patch",
            "Inhalation",
            "EA",
            "Gummy",
            "IU",
            "MG",
            "Cup",
            "L",
            "KG",
            "LB",
            "Minute",
            "Ounce"
        )

        binding.llDose.setOnClickListener {
            showDoseBottomSheet(doseUnits)
        }
    }
    private fun showDoseBottomSheet(doseUnits: List<String>) {

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dose, null)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvDose)
        val btnClose = view.findViewById<ImageView>(R.id.ivClose)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter = DoseAdapter(doseUnits) { selectedDose ->

            binding.tvDose.setText(selectedDose) // EditText

            // If spDose is TextView:
            // binding.spDose.text = selectedDose

            dialog.dismiss()
        }

        btnClose.setOnClickListener {
            dialog.dismiss()
            Log.e("Click5","Clic");
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showScheduleDoseSheet(times: Int) {

        val dialog = BottomSheetDialog(requireContext())

        val view = layoutInflater.inflate(
            R.layout.bottom_schedule_dose,
            null
        )

        val container =
            view.findViewById<LinearLayout>(R.id.llDoseContainer)

        val selectedTimeViews = mutableListOf<TextView>()

        for (i in 1..times) {

            val row = layoutInflater.inflate(
                R.layout.item_dose_time,
                container,
                false
            )

            val tvIntake =
                row.findViewById<TextView>(R.id.tvIntake)

            val tvTime =
                row.findViewById<TextView>(R.id.tvTime)

            selectedTimeViews.add(tvTime)

            tvIntake.text = when (i) {
                1 -> "1st intake"
                2 -> "2nd intake"
                3 -> "3rd intake"
                else -> "${i}th intake"
            }

            tvTime.setOnClickListener {
                openTimePicker(tvTime)
            }

            container.addView(row)
        }

        view.findViewById<TextView>(R.id.tvDoseCount)
            .text = "$times times"

        view.findViewById<Button>(R.id.btnDone)
            .setOnClickListener {

                timeList.clear()

                selectedTimeViews.forEach { tv ->

                    val time = tv.text.toString()

                    if (time.isNotEmpty() &&
                        time != "Select Time"
                    ) {
                        timeList.add(time)
                    }
                }

                // Show selected times on main screen
                binding.tvTime.text =
                    timeList.joinToString(", ")

                Log.e("TIME_LIST", timeList.toString())

                dialog.dismiss()
            }

        dialog.setContentView(view)

        dialog.show()
    }
    private fun openTimePicker(tv: TextView){

        val calendar = Calendar.getInstance()


        TimePickerDialog(
            requireContext(),
            { _, hour, minute ->


                val amPm =
                    if (hour >= 12) "PM" else "AM"


                val h =
                    if (hour > 12) hour - 12 else hour


                val selectedTime =
                    String.format("%02d:%02d %s", h, minute, amPm)

                tv.text = selectedTime

                if (!timeList.contains(selectedTime)) {
                    timeList.add(selectedTime)
                }


            },

            calendar.get(Calendar.HOUR),
            calendar.get(Calendar.MINUTE),
            false

        ).show()

    }
    class DoseAdapter(
        private val items: List<String>,
        private val onItemClick: (String) -> Unit
    ) : RecyclerView.Adapter<DoseAdapter.ViewHolder>()
    {

        inner class ViewHolder(val binding: SpinnerDropdownBlueBinding) :
            RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder {

            val binding = SpinnerDropdownBlueBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )

            return ViewHolder(binding)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) {

            holder.binding.text1.text = items[position]

            holder.binding.root.setOnClickListener {
                onItemClick(items[position])
            }
        }

        override fun getItemCount() = items.size
    }
    private fun setFrequency() {

        val frequencyList = listOf(
            "1 time, Daily",
            "2 times, Daily",
            "3 times, Daily",
            "More than 3 times, Daily",
            "Specific Days of the Week",
            "Every X Days",
            "Every X Weeks",
            "Every X Months"
        )

        binding.llFrequency.setOnClickListener {
            showFrequencyBottomSheet(frequencyList)
        }
        binding.cardScheduleTime.setOnClickListener {
            if (timesPerDay.equals("")){
                Toast.makeText(requireContext(), "Set Frequency First", Toast.LENGTH_SHORT).show()
            }else{
                showScheduleDoseSheet(timesPerDay.toInt())
            }

        }
    }

    private fun showFrequencyBottomSheet(list: List<String>) {

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(
            R.layout.bottom_sheet_frequency,
            null
        )

        val recyclerView = view.findViewById<RecyclerView>(R.id.rvFrequency)
        val ivClose = view.findViewById<ImageView>(R.id.ivClose)
        ivClose.setOnClickListener {
            dialog.dismiss()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        recyclerView.adapter =
            DoseAdapter(list) { selected ->

                binding.tvFrequency.text = selected

                dialog.dismiss()

                when (selected) {

                    "1 time, Daily" -> {
                        apiType = "1 time, Daily"
                        showScheduleDoseSheet(1)
                    }

                    "2 times, Daily" -> {
                        apiType = "2 times, Daily"
                        showScheduleDoseSheet(2)
                    }

                    "3 times, Daily" -> {
                        apiType = "3 times, Daily"
                        showScheduleDoseSheet(3)
                    }

                    "More than 3 times, Daily" -> {
                        apiType = "More than 3 times, Daily"
                        showMoreTimesPicker()
                    }

                    "Specific Days of the Week" -> {
                        apiType = "Specific Days of the Week"
                        showWeekDaysBottomSheet()
                    }

                    "Every X Days" -> {
                        apiType = "Every X Days"
                        showIntervalPickerBottomSheet(
                            title = "Choose Interval",
                            unit = "Days"
                        )
                    }

                    "Every X Weeks" -> {
                        apiType = "Every X Weeks"
                        showIntervalPickerBottomSheet(
                            title = "Choose Week Interval",
                            unit = "Weeks"
                        )
                    }

                    "Every X Months" -> {
                        apiType = "Every X Months"
                        showIntervalPickerBottomSheet(
                            title = "Choose Month Interval",
                            unit = "Months"
                        )
                    }
                }

                Log.e("API_TYPE", apiType)
            }


        dialog.setContentView(view)
        dialog.show()
    }

    private fun showIntervalPickerBottomSheet(
        title: String,
        unit: String
    ) {

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(
            R.layout.bottom_sheet_every_x_days,
            null
        )

        val tvTitle = view.findViewById<TextView>(R.id.tvTitle)
        val tvUnit = view.findViewById<TextView>(R.id.tvUnit)
        val npDays = view.findViewById<NumberPicker>(R.id.npDays)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        tvTitle.text = title
        tvUnit.text = unit

        npDays.minValue = 1
        npDays.maxValue = 30
        npDays.value = 1

        btnDone.setOnClickListener {

             intervalValue = npDays.value.toString()

            dialog.dismiss()

            showTimesPerDayBottomSheet(
                intervalValue = intervalValue.toInt(),
                unit = unit
            )
        }

        dialog.setContentView(view)
        dialog.show()
    }

    private fun showTimesPerDayBottomSheet(
        intervalValue: Int,
        unit: String
    ) {

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(
            R.layout.bottom_sheet_times_per_day,
            null
        )

        val npTimes = view.findViewById<NumberPicker>(R.id.npTimes)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        npTimes.minValue = 1
        npTimes.maxValue = 10
        npTimes.value = 1

        btnDone.setOnClickListener {

             timesPerDay = npTimes.value.toString()

            binding.tvFrequency.text =
                "Every $intervalValue $unit, $timesPerDay times/day"

            dialog.dismiss()

            showScheduleDoseSheet(timesPerDay.toInt())
        }

        dialog.setContentView(view)
        dialog.show()
    }
    private fun showWeekDaysBottomSheet() {

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(
            R.layout.bottom_sheet_week_days,
            null
        )

        val btnDone = view.findViewById<Button>(R.id.btnDone)
        val tvSelected = view.findViewById<TextView>(R.id.tvSelectedDays)

        val days = listOf(
            view.findViewById<TextView>(R.id.tvSun),
            view.findViewById<TextView>(R.id.tvMon),
            view.findViewById<TextView>(R.id.tvTue),
            view.findViewById<TextView>(R.id.tvWed),
            view.findViewById<TextView>(R.id.tvThu),
            view.findViewById<TextView>(R.id.tvFri),
            view.findViewById<TextView>(R.id.tvSat)
        )

        val labels = listOf(
            "Sun",
            "Mon",
            "Tue",
            "Wed",
            "Thu",
            "Fri",
            "Sat"
        )

        selectedDays.clear()

        for (i in days.indices) {

            days[i].setOnClickListener {

                val day = labels[i]

                if (selectedDays.contains(day)) {

                    selectedDays.remove(day)

                    days[i].setBackgroundResource(
                        R.drawable.bg_day_unselected
                    )

                    days[i].setTextColor(
                        resources.getColor(
                            R.color.black,
                            null
                        )
                    )

                } else {

                    selectedDays.add(day)

                    days[i].setBackgroundResource(
                        R.drawable.bg_day_selected
                    )

                    days[i].setTextColor(
                        resources.getColor(
                            R.color.white,
                            null
                        )
                    )
                }

                tvSelected.text =
                    if (selectedDays.isNotEmpty())
                        "Every ${selectedDays.joinToString(", ")}"
                    else
                        "Select Days"
            }
        }

        btnDone.setOnClickListener {



            weekDays = selectedDays.joinToString(",")

            binding.tvFrequency.text =
                if (selectedDays.isNotEmpty())
                    "Every ${selectedDays.joinToString(", ")}"
                else
                    "No days selected"

            Log.e("WEEK_DAYS", weekDays)
            showTimesPerDayBottomSheet(
                intervalValue = 1,
                unit = "specific_days"
            )
            dialog.dismiss()
        }

        dialog.setContentView(view)
        dialog.show()
    }
    private fun showMoreTimesPicker() {

        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_more_times, null)

        val picker = view.findViewById<NumberPicker>(R.id.numberPicker)
        val btnDone = view.findViewById<Button>(R.id.btnDone)

        picker.minValue = 1
        picker.maxValue = 10
        picker.value = 4

        btnDone.setOnClickListener {

            val selectedTimes = picker.value

            dialog.dismiss()

            // 👉 Now open schedule dynamically
            showScheduleDoseSheet(selectedTimes)
        }

        dialog.setContentView(view)
        dialog.show()
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

        val map = HashMap<String, String>()
        if (binding.tvStartDate.text.toString().isNotEmpty()
            && binding.tvEndDate.text.toString().isNotEmpty()
        ) {

          /*  try {
                val inputFormat = SimpleDateFormat("EEE dd MMM yyyy", Locale.ENGLISH)
                val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)

                val start = inputFormat.parse(binding.tvStartDate.text.toString())
                val end = inputFormat.parse(binding.tvEndDate.text.toString())

                formattedFromDate = start?.let { outputFormat.format(it) } ?: ""
                formattedToDate = end?.let { outputFormat.format(it) } ?: ""

            } catch (e: Exception) {
                e.printStackTrace()
            }*/

        }
        map["patient_id"] = user_id
        map["product_id"] = fld_product_id
        map["type"] = apiType
        map["pills_count"] = binding.tvPillsCnt.text.toString().trim()

        map["from_date"] = binding.tvStartDate.text.toString().trim()
        map["to_date"] = binding.tvEndDate.text.toString().trim()

        // Dose
        map["dose_unit"] = binding.tvDose.text.toString()

        // Intake times
        map["times"] = Gson().toJson(timeList)

        when (apiType) {

            "1 time, Daily",
            "2 times, Daily",
            "3 times, Daily",
            "More than 3 times, Daily" -> {

                map["times_per_day"] = timeList.size.toString()
            }

            "Specific Days of the Week" -> {

                map["week_days"] = selectedDays.joinToString(",")
                map["times_per_day"] = timeList.size.toString()
            }

            "Every X Days",
            "Every X Weeks",
            "Every X Months" -> {

                map["interval_value"] = intervalValue
                map["times_per_day"] = timesPerDay
            }
        }
        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }

    private fun params_details(): HashMap<String, String> {

        val map = HashMap<String, String>()

        map["patient_id"] = user_id
        map["product_id"] = fld_product_id

        for ((key, value) in map) {
            Log.e("TAG", "Key: $key:$value")
        }
        return map
    }









}

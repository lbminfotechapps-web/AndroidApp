package com.kotlin.dvijaypatient.ui.fragment.set_reminder

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
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.adapter.MedicineListAdapter
import com.kotlin.dvijaypatient.databinding.FragmentMedicineScheduleBinding
import com.kotlin.dvijaypatient.databinding.FragmentReminderBinding
import com.kotlin.dvijaypatient.databinding.LayoutSetReminderBinding
import com.kotlin.dvijaypatient.global.ClassGlobal
import com.kotlin.dvijaypatient.model.MedicineModel

import com.kotlin.dvijaypatient.ui.fragment.BaseFragment
import com.kotlin.dvijaypatient.utils.ClassConnectionDetector
import okhttp3.MultipartBody

import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SetReminderFragment : DialogFragment() {


    private lateinit var binding: LayoutSetReminderBinding

    private var type = ""

    private var minDate = ""


    interface OnDateSelectedListener {

        fun onDateSelected(date: String)

    }


    private var listener: OnDateSelectedListener? = null


    fun setListener(listener: OnDateSelectedListener) {

        this.listener = listener

    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.layout_set_reminder,
            container,
            false
        )


        return binding.root

    }




    override fun onStart() {

        super.onStart()


        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

    }





    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {


        super.onViewCreated(view, savedInstanceState)

        init()

    }






    private fun init(){


        type =
            arguments?.getString("TYPE") ?: ""


        minDate =
            arguments?.getString("MIN_DATE") ?: ""



        binding.tvHeading.text = type



        setupDatePickers()



        binding.ivOk.setOnClickListener {


            val selectedDate =
                binding.tvDate.text.toString()



            listener?.onDateSelected(
                selectedDate
            )


            dismiss()

        }




        binding.ivBack.setOnClickListener {


            dismiss()

        }


    }









    private fun setupDatePickers(){


        val calendar =
            Calendar.getInstance()



        val currentDay =
            calendar.get(Calendar.DAY_OF_MONTH)


        val currentMonth =
            calendar.get(Calendar.MONTH)


        val currentYear =
            calendar.get(Calendar.YEAR)




        val months = arrayOf(

            "Jan","Feb","Mar",
            "Apr","May","Jun",
            "Jul","Aug","Sep",
            "Oct","Nov","Dec"

        )





        binding.dayPicker.minValue = 1

        binding.dayPicker.maxValue = 31

        binding.dayPicker.value =
            currentDay






        binding.monthPicker.minValue = 0

        binding.monthPicker.maxValue = 11

        binding.monthPicker.displayedValues =
            months

        binding.monthPicker.value =
            currentMonth






        binding.yearPicker.minValue = 2000

        binding.yearPicker.maxValue = 2100

        binding.yearPicker.value =
            currentYear





        binding.dayPicker.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS


        binding.monthPicker.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS


        binding.yearPicker.descendantFocusability =
            NumberPicker.FOCUS_BLOCK_DESCENDANTS






        updateDays()



        updateSelectedDateText()






        binding.dayPicker.setOnValueChangedListener { _,_,_ ->

            updateSelectedDateText()

        }



        binding.monthPicker.setOnValueChangedListener { _,_,_ ->


            updateDays()

            updateSelectedDateText()


        }




        binding.yearPicker.setOnValueChangedListener { _,_,_ ->


            updateDays()

            updateSelectedDateText()


        }






        // END DATE VALIDATION

        if(type == "End Date" && minDate.isNotEmpty()){


            applyMinimumDate()


        }


    }









    private fun updateDays(){


        val day =
            binding.dayPicker.value


        val month =
            binding.monthPicker.value


        val year =
            binding.yearPicker.value



        val calendar =
            Calendar.getInstance()



        calendar.set(
            year,
            month,
            day
        )



        val maxDay =
            calendar.getActualMaximum(
                Calendar.DAY_OF_MONTH
            )



        binding.dayPicker.maxValue =
            maxDay




        if(day > maxDay){


            binding.dayPicker.value =
                maxDay

        }


    }









    private fun applyMinimumDate(){



        try {


            val sdf =
                SimpleDateFormat(
                    "EEE dd MMM yyyy",
                    Locale.getDefault()
                )



            val minCalendar =
                Calendar.getInstance()



            minCalendar.time =
                sdf.parse(minDate)!!





            val minYear =
                minCalendar.get(
                    Calendar.YEAR
                )


            val minMonth =
                minCalendar.get(
                    Calendar.MONTH
                )


            val minDay =
                minCalendar.get(
                    Calendar.DAY_OF_MONTH
                )






            binding.yearPicker.minValue =
                minYear




            if(
                binding.yearPicker.value == minYear
            ){


                binding.monthPicker.minValue =
                    minMonth


            }





            if(
                binding.yearPicker.value == minYear &&
                binding.monthPicker.value == minMonth
            ){


                binding.dayPicker.minValue =
                    minDay


            }



        }catch(e:Exception){


            e.printStackTrace()

        }


    }









    private fun updateSelectedDateText(){



        val day =
            binding.dayPicker.value


        val month =
            binding.monthPicker.value


        val year =
            binding.yearPicker.value





        val months = arrayOf(

            "Jan","Feb","Mar",
            "Apr","May","Jun",
            "Jul","Aug","Sep",
            "Oct","Nov","Dec"

        )





        val calendar =
            Calendar.getInstance()



        calendar.set(
            year,
            month,
            day
        )





        val dayName =
            SimpleDateFormat(
                "EEE",
                Locale.getDefault()
            )
                .format(calendar.time)





        binding.tvDate.text =

            "$dayName $day ${months[month]} $year"



    }


}
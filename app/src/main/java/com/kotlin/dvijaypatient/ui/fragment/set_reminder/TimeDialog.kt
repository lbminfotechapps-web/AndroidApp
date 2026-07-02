package com.kotlin.dvijaypatient.ui.fragment.set_reminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import com.kotlin.dvijaypatient.databinding.TimeDialogBinding
import java.util.Calendar

class TimeDialog : DialogFragment() {

    private lateinit var binding: TimeDialogBinding

    interface OnTimeSelectedListener {
        fun onTimeSelected(time: String)
    }

    private var listener: OnTimeSelectedListener? = null

    fun setListener(listener: OnTimeSelectedListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TimeDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupTimePicker()
        setupClicks()
    }

    // ✅ 12 Hour Setup
    private fun setupTimePicker() {

        val calendar = Calendar.getInstance()
        val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val isPM = hour24 >= 12
        val hour12 = when {
            hour24 == 0 -> 12
            hour24 > 12 -> hour24 - 12
            else -> hour24
        }


        binding.npHour.minValue = 1
        binding.npHour.maxValue = 12
        binding.npHour.value = hour12
        binding.npHour.wrapSelectorWheel = true


        binding.npMinute.minValue = 0
        binding.npMinute.maxValue = 59
        binding.npMinute.value = minute
        binding.npMinute.wrapSelectorWheel = true


        val amPm = arrayOf("AM", "PM")
        binding.npAmPm.minValue = 0
        binding.npAmPm.maxValue = 1
        binding.npAmPm.displayedValues = amPm
        binding.npAmPm.value = if (isPM) 1 else 0


        binding.npHour.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.npMinute.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.npAmPm.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS


        updateTimeText()


        binding.npHour.setOnValueChangedListener { _, _, _ -> updateTimeText() }
        binding.npMinute.setOnValueChangedListener { _, _, _ -> updateTimeText() }
        binding.npAmPm.setOnValueChangedListener { _, _, _ -> updateTimeText() }
    }

    // ✅ Format Time
    private fun updateTimeText() {

        val hour = binding.npHour.value
        val minute = binding.npMinute.value
        val amPm = if (binding.npAmPm.value == 0) "AM" else "PM"

        val formattedTime = String.format("%02d:%02d %s", hour, minute, amPm)

        binding.tvSelectedTime.text = formattedTime
    }

    // ✅ Clicks
    private fun setupClicks() {

        // Back
        binding.topBar.getChildAt(0).setOnClickListener {
            dismiss()
        }

        // OK
        binding.topBar.getChildAt(2).setOnClickListener {

            val time = binding.tvSelectedTime.text.toString()

            listener?.onTimeSelected(time)

            dismiss()
        }
    }
}
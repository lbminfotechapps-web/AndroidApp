package com.kotlin.dvijaypatient.ui.fragment.set_reminder

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.kotlin.dvijaypatient.databinding.DialogWeekDaysBinding

class WeekDays : DialogFragment() {

    private lateinit var binding: DialogWeekDaysBinding

    // 👉 Selected days
    private val selectedDays = mutableSetOf<String>()

    // 👉 Previously selected days (from Fragment)
    private var preSelectedDays: Set<String> = emptySet()

    // 👉 Listener
    interface OnDaysSelectedListener {
        fun onDaysSelected(days: String)
    }

    private var listener: OnDaysSelectedListener? = null

    fun setListener(listener: OnDaysSelectedListener) {
        this.listener = listener
    }

    fun setPreSelectedDays(days: Set<String>) {
        preSelectedDays = days
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogWeekDaysBinding.inflate(inflater, container, false)
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
        setupClicks()
        restoreSelection() // 🔥 IMPORTANT
    }

    // ✅ Setup clicks
    private fun setupClicks() {

        setupDay(binding.daySunday, binding.imgSunday, "Sunday")
        setupDay(binding.dayMonday, binding.imgMonday, "Monday")
        setupDay(binding.dayTuesday, binding.imgTuesday, "Tuesday")
        setupDay(binding.dayWednesday, binding.imgWednesday, "Wednesday")
        setupDay(binding.dayThursday, binding.imgThursday, "Thursday")
        setupDay(binding.dayFriday, binding.imgFriday, "Friday")
        setupDay(binding.daySaturday, binding.imgSaturday, "Saturday")

        // 👉 Clear
        binding.btnClear.setOnClickListener {
            selectedDays.clear()
            refreshChecks()
        }

        // 👉 OK
        binding.ivOk.setOnClickListener {

            val result = selectedDays.joinToString(", ")

            listener?.onDaysSelected(result)

            dismiss()
        }

        // 👉 Back
        binding.ivBack.setOnClickListener {
            dismiss()
        }
    }

    // ✅ Handle click per day
    private fun setupDay(layout: View, check: ImageView, day: String) {

        layout.setOnClickListener {

            if (selectedDays.contains(day)) {
                selectedDays.remove(day)
                check.visibility = View.GONE
            } else {
                selectedDays.add(day)
                check.visibility = View.VISIBLE
            }
        }
    }

    // ✅ Restore previous selection
    private fun restoreSelection() {

        selectedDays.clear()
        selectedDays.addAll(preSelectedDays)

        fun setChecked(day: String, view: ImageView) {
            view.visibility =
                if (selectedDays.contains(day)) View.VISIBLE else View.GONE
        }

        setChecked("Sunday", binding.imgSunday)
        setChecked("Monday", binding.imgMonday)
        setChecked("Tuesday", binding.imgTuesday)
        setChecked("Wednesday", binding.imgWednesday)
        setChecked("Thursday", binding.imgThursday)
        setChecked("Friday", binding.imgFriday)
        setChecked("Saturday", binding.imgSaturday)
    }

    // ✅ Clear UI
    private fun refreshChecks() {
        binding.imgSunday.visibility = View.GONE
        binding.imgMonday.visibility = View.GONE
        binding.imgTuesday.visibility = View.GONE
        binding.imgWednesday.visibility = View.GONE
        binding.imgThursday.visibility = View.GONE
        binding.imgFriday.visibility = View.GONE
        binding.imgSaturday.visibility = View.GONE
    }
}
package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.MedicineCheckModel
import com.kotlin.dvijaypatient.model.MissdosedetailsResponse
import java.util.Locale

class MedicineCheckAdapter(
    private val list: MutableList<MissdosedetailsResponse>,
    private val listener: OnMedicineCheckListener
) : RecyclerView.Adapter<MedicineCheckAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvMedicineName)
        val radioGroup: RadioGroup = view.findViewById(R.id.radioGroup)
        val rbYes: RadioButton = view.findViewById(R.id.rbYes)
        val rbNo: RadioButton = view.findViewById(R.id.rbNo)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    interface OnMedicineCheckListener {
        fun onMedicineChecked(item: MissdosedetailsResponse)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medicine_check, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvName.text = item.fld_prod_name
        holder.tvDate.text = "Missing Date & Time "+formatDate(item.fld_schedule_date)+" - "+item.fld_time

        holder.radioGroup.setOnCheckedChangeListener(null)

        when (item.isTaken) {
            "Yes" -> holder.rbYes.isChecked = true
            "No" -> holder.rbNo.isChecked = true
            else -> holder.radioGroup.clearCheck()
        }

        holder.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            item.isTaken = when (checkedId) {
                R.id.rbYes -> "Yes"
                R.id.rbNo -> "No"
                else -> ""

            }
            listener.onMedicineChecked(item)
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}
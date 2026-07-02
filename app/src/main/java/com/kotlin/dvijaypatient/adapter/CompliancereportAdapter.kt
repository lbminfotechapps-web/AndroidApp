package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.MedicineCheckModel
import com.kotlin.dvijaypatient.model.MissdosedetailsResponse
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.Locale

class CompliancereportAdapter(
    private val list: MutableList<CompliancereportDetails>,
) : RecyclerView.Adapter<CompliancereportAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvMedicineName: TextView = view.findViewById(R.id.tvMedicineName)
        val tvSchedule: TextView = view.findViewById(R.id.tvSchedule)
        val imgCheck: ImageView = view.findViewById(R.id.imgCheck)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_compliance_report, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        if(HomeFragment.complianceType.equals("missed"))
        {
            holder.imgCheck.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.closes)
            )
        }else if(HomeFragment.complianceType.equals("reminder"))
        {
            holder.imgCheck.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.notification_bell)
            )
        }else{
            holder.imgCheck.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.check)
            )
        }
        holder.tvTime.setText(item.fld_time)
        holder.tvMedicineName.setText(item.fld_prod_name)
        holder.tvSchedule.setText("● SCHEDULE \n     "+item.fld_schedule_date)


    }

    fun formatDate(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}
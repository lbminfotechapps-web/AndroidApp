package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.CompliancereportDetails
import com.kotlin.dvijaypatient.model.DateWiseScheduleResponse
import com.kotlin.dvijaypatient.model.MedicineCheckModel
import com.kotlin.dvijaypatient.model.MissdosedetailsResponse
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.Locale

class DateWiseScheduleAdapter(
    private val list: MutableList<DateWiseScheduleResponse>,
    private val listener: OnScheduleClickListener
) : RecyclerView.Adapter<DateWiseScheduleAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvMedicineName: TextView = view.findViewById(R.id.tvMedicineName)
        val tvSchedule: TextView = view.findViewById(R.id.tvSchedule)
        val imgCheck: ImageView = view.findViewById(R.id.imgCheck)
        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)
        val rlMain: RelativeLayout = view.findViewById(R.id.rlMain)

    }
    interface OnScheduleClickListener {

        fun onScheduleClick(
            item: DateWiseScheduleResponse,
            position: Int
        )

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_wise_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        if(item.fld_status.equals("0"))
        {
            holder.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.closes)
            )
        }else if(item.fld_status.equals("1"))
        {
            holder.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.check)
            )
        }else{
            holder.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.notification_bell)
            )
        }

        holder.rlMain.setOnClickListener {


            listener.onScheduleClick(
                item,
                position
            )

        }
        holder.tvTime.setText(item.fld_dose_unit+" ,"+item.fld_time)
        holder.tvMedicineName.setText(item.fld_prod_name)
        holder.tvSchedule.setText("● SCHEDULE \n     "+item.fld_schedule_date)





    }


}
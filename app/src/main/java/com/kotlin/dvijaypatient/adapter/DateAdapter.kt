package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.MissingdatesResponse
import java.util.Locale

class DateAdapter(
    private val list: List<MissingdatesResponse>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<DateAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.date_row, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val date = list[position]
        holder.tvDate.text = formatDate(date.fld_schedule_date)

        holder.itemView.setOnClickListener {
            onClick(date.fld_schedule_date)
        }
    }

    fun formatDate(inputDate: String): String {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val date = inputFormat.parse(inputDate)
        return outputFormat.format(date!!)
    }
}
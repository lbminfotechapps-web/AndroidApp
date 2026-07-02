package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.WeightResponse

class WeightAdapter(
    private val list: List<WeightResponse>

): RecyclerView.Adapter<WeightAdapter.ViewHolder>() {


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val tvDateTime =
            view.findViewById<TextView>(R.id.tvDateTime)

        val tvWeight =
            view.findViewById<TextView>(R.id.tvWeight)

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_weight_details,
                parent,
                false
            )

        return ViewHolder(view)

    }


    override fun getItemCount()=list.size


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ){

        val item=list[position]


        holder.tvDateTime.text =
            "${item.fld_date} | ${item.fld_time}"


        holder.tvWeight.text =
            "${item.fld_value} Kg"

    }

}
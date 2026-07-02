package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.BPResponse

class BPAdapter(
    private val list: List<BPResponse>
): RecyclerView.Adapter<BPAdapter.ViewHolder>(){


    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){

        val tvDateTime=view.findViewById<TextView>(R.id.tvDateTime)
        val tvSys=view.findViewById<TextView>(R.id.tvSys)
        val tvDia=view.findViewById<TextView>(R.id.tvDia)
        val tvPulse=view.findViewById<TextView>(R.id.tvPulse)

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):ViewHolder{


        val view= LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bp_details,parent,false)

        return ViewHolder(view)

    }



    override fun getItemCount()=list.size



    override fun onBindViewHolder(
        holder:ViewHolder,
        position:Int
    ){

        val item=list[position]


        holder.tvDateTime.text =
            "${item.fld_date} | ${item.fld_time}"


        holder.tvSys.text =
            "SYS\n${item.fld_value}"


        holder.tvDia.text =
            "DIA\n${item.fld_dia_value}"


        holder.tvPulse.text =
            "PUL\n${item.fld_pulse_value}"


    }

}
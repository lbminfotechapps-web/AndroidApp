package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.SugarResponse

class MedicalDetailsAdapter(
    private val list: List<SugarResponse>

) : RecyclerView.Adapter<MedicalDetailsAdapter.ViewHolder>() {



    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view){


        val tvSugarType:TextView =
            view.findViewById(R.id.tvSugarType)


        val tvDateTime:TextView =
            view.findViewById(R.id.tvDateTime)


        val tvSugarValue:TextView =
            view.findViewById(R.id.tvSugarValue)

    }



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {


        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_sugar_details,
                parent,
                false
            )


        return ViewHolder(view)

    }



    override fun getItemCount(): Int {

        return list.size

    }



    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int

    ){

        val item = list[position]


        holder.tvSugarType.text =
            item.fld_sugar_type


        holder.tvDateTime.text =
            "${item.fld_date} | ${item.fld_time}"


        holder.tvSugarValue.text =
            item.fld_value


    }


}
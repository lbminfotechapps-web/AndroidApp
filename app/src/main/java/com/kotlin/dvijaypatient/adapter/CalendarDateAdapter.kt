package com.kotlin.dvijaypatient.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.databinding.ItemDateBinding
import com.kotlin.dvijaypatient.model.DateModel
import java.util.Calendar

class CalendarDateAdapter(
    private val dateList: List<DateModel>,
    private val onDateClick: (DateModel, Int) -> Unit
) : RecyclerView.Adapter<CalendarDateAdapter.DateViewHolder>() {


    private var selectedPosition =
        Calendar.getInstance()
            .get(Calendar.DAY_OF_MONTH) - 1



    inner class DateViewHolder(
        val binding: ItemDateBinding
    ) : RecyclerView.ViewHolder(binding.root)



    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateViewHolder {


        val binding = ItemDateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )


        return DateViewHolder(binding)

    }

    fun setSelectedDate(position: Int){

        val oldPosition = selectedPosition

        selectedPosition = position


        notifyItemChanged(oldPosition)

        notifyItemChanged(selectedPosition)

    }
    fun findDatePosition(
        day: Int,
        month: Int,
        year: Int
    ): Int {


        return dateList.indexOfFirst {


            it.date.toInt() == day
                    &&
                    it.month == month
                    &&
                    it.year == year


        }

    }
    override fun onBindViewHolder(
        holder: DateViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {


        val item = dateList[position]


        holder.binding.tvDay.text = item.day

        holder.binding.tvDate.text = item.date



        if (selectedPosition == position) {


            holder.binding.tvDate.setBackgroundResource(
                R.drawable.bg_selected_date
            )


            holder.binding.tvDate.setTextColor(
                Color.WHITE
            )


            holder.binding.tvDay.setTextColor(
                Color.parseColor("#4285F4")
            )


        } else {


            holder.binding.tvDate.background = null


            holder.binding.tvDate.setTextColor(
                Color.BLACK
            )


            holder.binding.tvDay.setTextColor(
                Color.GRAY
            )

        }



        holder.itemView.setOnClickListener {


            val oldPosition = selectedPosition


            selectedPosition = position


            notifyItemChanged(oldPosition)

            notifyItemChanged(selectedPosition)



            onDateClick(
                item,
                position
            )

        }

    }



    override fun getItemCount(): Int {

        return dateList.size

    }


}
package com.kotlin.dvijaypatient.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.databinding.ItemTimeBinding

class TimeAdapter(
    private val list: MutableList<String>,
    private val onDelete: (Int) -> Unit
) : RecyclerView.Adapter<TimeAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val time = list[position]

        holder.binding.tvTime.text = time

        holder.binding.ivDelete.setOnClickListener {
            onDelete(position)
        }
    }
}
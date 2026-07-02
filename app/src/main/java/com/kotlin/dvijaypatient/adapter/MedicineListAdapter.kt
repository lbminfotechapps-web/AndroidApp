package com.kotlin.dvijaypatient.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.dvijaypatient.databinding.ItemMedicineBinding
import com.kotlin.dvijaypatient.model.MedicineModel

class MedicineListAdapter(
    private val context: Context,
    private  val onStatusClickListener: OnStatusClickListener
) : ListAdapter<MedicineModel, MedicineListAdapter.DataViewHolder>(DiffCallback()) {

    // Persistent map to save qty for each material_id
    val qtyMap = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemMedicineBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DataViewHolder(binding, onStatusClickListener)
    }

    interface OnStatusClickListener {
        fun onStatusClick(item: MedicineModel, position: Int, product_name: String)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(
        private val binding: ItemMedicineBinding,
        private val onStatusClickListener: OnStatusClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var textWatcher: TextWatcher? = null

        fun bind(item: MedicineModel)
        {
            binding.tvMedicineName.text = item.fld_prod_name
            binding.tvTime.setText(item.fld_days)
            binding.tvDateRange.text =
                "${item.fld_from_date} To ${item.fld_to_date}"

            binding.cardMedicine.setOnClickListener {
                onStatusClickListener.onStatusClick(item, adapterPosition, item.fld_prod_name)
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<MedicineModel>() {
        override fun areItemsTheSame(oldItem: MedicineModel, newItem: MedicineModel) =
            oldItem.time == newItem.time

        override fun areContentsTheSame(oldItem: MedicineModel, newItem: MedicineModel) =
            oldItem == newItem


        // Helper to get all items with non-empty qty for submission

    }
}

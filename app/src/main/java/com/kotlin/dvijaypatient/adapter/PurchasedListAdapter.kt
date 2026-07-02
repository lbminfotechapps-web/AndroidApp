package com.kotlin.dvijaypatient.adapter

import android.content.Context
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.databinding.ItemInvoiceHistoryBinding
import com.kotlin.dvijaypatient.model.PurchasedDetailsResponse
import com.kotlin.dvijaypatient.model.PurchasedHistoryResponse

class PurchasedListAdapter(
    private val context: Context,
    private  val onStatusClickListener: OnStatusClickListener
) : ListAdapter<PurchasedHistoryResponse, PurchasedListAdapter.DataViewHolder>(DiffCallback()) {

    // Persistent map to save qty for each material_id
    val qtyMap = mutableMapOf<String, String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemInvoiceHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DataViewHolder(binding, onStatusClickListener)
    }

    interface OnStatusClickListener {
        fun onStatusClick(item: List<PurchasedDetailsResponse>, position: Int)
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DataViewHolder(
        private val binding: ItemInvoiceHistoryBinding,
        private val onStatusClickListener: OnStatusClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        private var textWatcher: TextWatcher? = null

        fun bind(item: PurchasedHistoryResponse) {

            binding.tvInvoiceNo.text =
                item.Prefix + " - " + item.InvNo


            binding.tvBranch.text =
                "Branch Name : " + item.fld_branch_name


            binding.tvAmount.text =
                "₹ " + item.InvAmt


            try {



                binding.tvDate.setText(item.InvDate)




            } catch (e: Exception) {

                binding.tvDate.text = "-"


            }


            binding.tvView.setOnClickListener {

                onStatusClickListener.onStatusClick(
                    item.details,
                    adapterPosition
                )

            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<PurchasedHistoryResponse>() {
        override fun areItemsTheSame(oldItem: PurchasedHistoryResponse, newItem: PurchasedHistoryResponse) =
            oldItem.InvNo == newItem.InvNo

        override fun areContentsTheSame(oldItem: PurchasedHistoryResponse, newItem: PurchasedHistoryResponse) =
            oldItem == newItem


        // Helper to get all items with non-empty qty for submission

    }
}

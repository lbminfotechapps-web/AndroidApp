package com.kotlin.dvijaypatient.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.DocumentsModel
import com.kotlin.dvijaypatient.ui.fragment.home.HomeFragment
import java.text.SimpleDateFormat
import java.util.Locale

class DocumentsAdapter(
    private val list: MutableList<DocumentsModel>,
) : RecyclerView.Adapter<DocumentsAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTitle: TextView = view.findViewById(R.id.txtTitle)
        val txtInfo: TextView = view.findViewById(R.id.txtInfo)
        val imgIcon: ImageView = view.findViewById(R.id.imgIcon)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_documents, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        if(item.fld_media_type.equals("image"))
        {
            holder.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.gallery_new)
            )
        }
       else if(item.fld_media.endsWith(".pdf"))
        {
            holder.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.pdf)
            )
        }
        else
        {
            holder.imgIcon.setImageDrawable(
                ContextCompat.getDrawable(holder.itemView.context, R.drawable.xls)
            )
        }
        holder.txtTitle.setText(item.fld_prod_name)

        holder.txtInfo.setText("Media Type : "+item.fld_media_type)

        holder.itemView.setOnClickListener {

            val url = item.fld_media
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(url)

            when {
                url.endsWith(".mp4") -> {
                    intent.setDataAndType(uri, "video/*")
                }

                url.endsWith(".jpg") || url.endsWith(".jpeg") || url.endsWith(".png") -> {
                    intent.setDataAndType(uri, "image/*")
                }

                url.endsWith(".pdf") -> {
                    intent.setDataAndType(uri, "application/pdf")
                }

                url.endsWith(".xlsx") -> {
                    intent.setDataAndType(
                        uri,
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    )
                }

                url.endsWith(".xls") -> {
                    intent.setDataAndType(uri, "application/vnd.ms-excel")
                }

                else -> {
                    intent.setDataAndType(uri, "*/*")
                }
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            try {
                holder.itemView.context.startActivity(
                    Intent.createChooser(intent, "Open File")
                )

            } catch (e: Exception) {

                // 🔥 Fallback: open in browser / Google Docs
                try {
                    val googleDocsUrl =
                        "https://docs.google.com/gview?embedded=true&url=$url"

                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(googleDocsUrl)
                    )

                    holder.itemView.context.startActivity(browserIntent)

                } catch (ex: Exception) {
                    Toast.makeText(holder.itemView.context, "No app found to open this file", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

}
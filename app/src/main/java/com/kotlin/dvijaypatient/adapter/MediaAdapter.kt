package com.kotlin.dvijaypatient.adapter

import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.text.format.Formatter.formatFileSize
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kotlin.dvijaypatient.R
import com.kotlin.dvijaypatient.model.MediaModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.internal.concurrent.formatDuration
import java.net.HttpURLConnection
import java.net.URL

class MediaAdapter(private val list: List<MediaModel>) :
    RecyclerView.Adapter<MediaAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgMedia: ImageView = view.findViewById(R.id.imgMedia)
        val imgPlay: ImageView = view.findViewById(R.id.imgPlay)
        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
        val tvDuration: TextView = view.findViewById(R.id.tvDuration)
        val tvType: TextView = view.findViewById(R.id.tvType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_media_list, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvProductName.text = item.fld_prod_name

        // Check media type
        if (item.fld_media.endsWith(".mp4")) {
            holder.imgPlay.visibility = View.VISIBLE

            // Load thumbnail for video (use Glide)
            Glide.with(holder.itemView.context)
                .load(item.fld_media)
                .placeholder(R.drawable.ic_video_placeholder)
                .into(holder.imgMedia)
            holder.tvType.setText("Video")
            holder.tvDuration.setText("Duration : "+getVideoDuration(item.fld_media))

        } else {
            holder.imgPlay.visibility = View.GONE

            Glide.with(holder.itemView.context)
                .load(item.fld_media)
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.imgMedia)
            holder.tvType.setText("Image")
        }

        // Click Event
        holder.itemView.setOnClickListener {
            if (item.fld_media.endsWith(".mp4")) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(item.fld_media), "video/*")
                holder.itemView.context.startActivity(intent)
            } else {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(item.fld_media), "image/*")
                holder.itemView.context.startActivity(intent)
            }
        }



    }

    fun getVideoDuration(videoUrl: String): String {
        return try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(videoUrl, HashMap())

            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
            val timeInMillis = time?.toLong() ?: 0

            retriever.release()

            formatDuration(timeInMillis)

        } catch (e: Exception) {
            e.printStackTrace()
            "00:00"
        }
    }
    fun formatDuration(duration: Long): String {
        val seconds = (duration / 1000) % 60
        val minutes = (duration / (1000 * 60)) % 60
        val hours = (duration / (1000 * 60 * 60))

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }


}
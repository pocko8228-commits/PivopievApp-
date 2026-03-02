package com.pivopiev.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class VideoAdapter(
    private val onVideoClick: (VideoItem) -> Unit
) : ListAdapter<VideoItem, VideoAdapter.VideoViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_video, parent, false)
        return VideoViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val thumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val date: TextView = itemView.findViewById(R.id.tvDate)
        private val playIcon: View = itemView.findViewById(R.id.playIcon)

        fun bind(item: VideoItem) {
            title.text = item.title
            date.text = formatDate(item.publishedAt)

            Glide.with(itemView.context)
                .load(item.thumbnailUrl)
                .placeholder(R.drawable.thumb_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(150))
                .centerCrop()
                .into(thumbnail)

            itemView.setOnClickListener { onVideoClick(item) }
        }

        private fun formatDate(isoDate: String): String {
            return try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(isoDate) ?: return isoDate
                val formatter = SimpleDateFormat("d MMM yyyy", Locale("bg", "BG"))
                formatter.format(date)
            } catch (e: Exception) {
                isoDate.take(10)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<VideoItem>() {
            override fun areItemsTheSame(old: VideoItem, new: VideoItem) = old.videoId == new.videoId
            override fun areContentsTheSame(old: VideoItem, new: VideoItem) = old == new
        }
    }
}

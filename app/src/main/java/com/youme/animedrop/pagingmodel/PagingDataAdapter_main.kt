package com.youme.animedrop.pagingmodel

import android.os.Handler
import android.os.Looper
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.youme.animedrop.R
import com.youme.animedrop.data.model.AnimeCountdown
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AnimePagingAdapter(private val onItemClicker: (AnimeCountdown) -> Unit) : PagingDataAdapter<AnimeCountdown, AnimePagingAdapter.AnimeViewHolder>(
    object : DiffUtil.ItemCallback<AnimeCountdown>() {
        override fun areItemsTheSame(oldItem: AnimeCountdown, newItem: AnimeCountdown): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AnimeCountdown, newItem: AnimeCountdown): Boolean = oldItem == newItem
    }
) {

    override fun onBindViewHolder(holder: AnimeViewHolder, position: Int) {
        val item = getItem(position)
        if (item != null) {
            holder.bind(item)
            preloadNextItems(position,holder)
        }
    }
    private fun preloadNextItems(position: Int, holder: AnimeViewHolder) {
        val preloadCount = 10
        val itemCount = snapshot().items.size
        for (i in 1..preloadCount) {
            if (position + i >= itemCount) break
            try {
                val nextItem = getItem(position + i)
                nextItem?.let { Anime ->
                    Glide.with(holder.itemView.context)
                        .load(Anime.imageUrl)
                        .preload()
                }
            } catch (e: IndexOutOfBoundsException) {
                break
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.animebox, parent, false)
        return AnimeViewHolder(view,onItemClicker)
    }
    class AnimeViewHolder(itemView: View,private val onItemClicker: (AnimeCountdown) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)
        private val day: TextView = itemView.findViewById(R.id.textView1)
        private val hours: TextView = itemView.findViewById(R.id.textView3)
        private val min: TextView = itemView.findViewById(R.id.textView5)
        private val name: TextView = itemView.findViewById(R.id.textView)
        private val secs: TextView = itemView.findViewById(R.id.textView7)

        private val handler = Handler(Looper.getMainLooper())
        private var currentAnime: AnimeCountdown? = null
        fun bind(Anime: AnimeCountdown) {
            currentAnime = Anime
            Glide.with(itemView.context).load(Anime.imageUrl).into(imageView)
            updateCountdown(Anime)
            name.text=Anime.title
            itemView.setOnClickListener { onItemClicker(Anime) }

            handler.postDelayed(object : Runnable {
                override fun run() {
                    currentAnime?.let { updateCountdown(it) }
                    handler.postDelayed(this, 1000)
                }
            }, 1000)
        }
        private fun updateCountdown(Anime: AnimeCountdown) {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")
            val airDate = LocalDateTime.parse(Anime.releaseDateTime.toString(), formatter)
            val now = LocalDateTime.now()
            val duration = Duration.between(now, airDate)

            val days = duration.toDays()
            val hourss = duration.minusDays(days).toHours()
            val minutes = duration.minusDays(days).minusHours(hourss).toMinutes()
            val seconds = duration.minusDays(days).minusHours(hourss).minusMinutes(minutes).seconds

            day.text = "$days"
            hours.text = "$hourss"
            min.text = "$minutes"
            secs.text = "$seconds"
        }

        fun stopCountdown() {
            handler.removeCallbacksAndMessages(null)
        }
    }
}

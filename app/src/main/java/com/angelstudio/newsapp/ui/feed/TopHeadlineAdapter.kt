package com.angelstudio.newsapp.ui.feed

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.databinding.TopHeadlineItemBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TopHeadlineAdapter(val clickListener: TopHeadlineListener,val  lifecycle: Lifecycle) :ListAdapter<Article,TopHeadlineAdapter.TopHeadlineViewHolder>(TopHeadlineDiffCallback()) {

    override fun onBindViewHolder(holder: TopHeadlineViewHolder, position: Int) {


        holder.bind(getItem(position),clickListener)

        if(  getItem(position)?.source?.name.equals("Youtube.com")){
            getItem(position).url?.let { holder.cueVideo(it) } }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHeadlineViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding = TopHeadlineItemBinding.inflate(layoutInflater,parent,false)
        lifecycle.addObserver(binding.youtubePlayerView)

        return TopHeadlineViewHolder(binding)
    }

    class TopHeadlineViewHolder(val binding: TopHeadlineItemBinding): RecyclerView.ViewHolder(binding.root){
        private var youTubePlayer: YouTubePlayer? = null
        private var currentVideoId: String? = null

        init {
            binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer

                }
            })

        }
        fun bind(item: Article, clickListener: TopHeadlineListener){
            binding.article= item
            binding.clickListener =clickListener
            binding.executePendingBindings()
        }
        fun cueVideo(urlsrc: String) {
            val link = urlsrc
            val url = link.split("v=".toRegex()).dropLastWhile { it.isEmpty()
            }.toTypedArray().get(1)

            currentVideoId = url

            if (youTubePlayer == null)
                return

            youTubePlayer!!.cueVideo(url, 0f)
        }



    }
}

class TopHeadlineDiffCallback :DiffUtil.ItemCallback<Article>(){
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.title == newItem.title
    }
    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}

class TopHeadlineListener(val clickListener: (url: String,source :String) -> Unit){
    fun onClick(article: Article )= clickListener(article.url!!,article.source.name!!)




}



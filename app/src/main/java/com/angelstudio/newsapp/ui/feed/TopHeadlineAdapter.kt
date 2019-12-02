package com.angelstudio.newsapp.ui.feed

import android.content.Context
import android.content.SharedPreferences
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

class TopHeadlineAdapter(val clickListener: TopHeadlineListener,val archiveListener : ArchiveListener,val  lifecycle: Lifecycle,val cxt : Context) :ListAdapter<Article,TopHeadlineAdapter.TopHeadlineViewHolder>(TopHeadlineDiffCallback()) {

    private val preferences: SharedPreferences
        get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(cxt)

    override fun onBindViewHolder(holder: TopHeadlineViewHolder, position: Int) {

        val country =preferences.getString("Country","")

        holder.bind(getItem(position),clickListener,archiveListener,country)

        if(  getItem(position)?.source?.name.equals("Youtube.com")){
            getItem(position).url?.let {
                holder.cueVideo(it) }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHeadlineViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding = TopHeadlineItemBinding.inflate(layoutInflater,parent,false)

        lifecycle.addObserver(binding.youtubePlayerView)

        return TopHeadlineViewHolder(binding)
    }

    class TopHeadlineViewHolder(val binding: TopHeadlineItemBinding): RecyclerView.ViewHolder(binding.root){
        private var currentVideoId: String? = null
        private var youTubePlayer: YouTubePlayer? = null
/*
        init {
             binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                    youTubePlayer = initializedYouTubePlayer
                 }
            })
       }  */
        fun bind(item: Article, clickListener: TopHeadlineListener,archiveListener:ArchiveListener,country : String?){

            var item1 :Article =item
            if(country.equals("eg") || country.equals("jp") || country.equals("ae")
                || country.equals("il") || country.equals("hk")   ){
                item1.content=null
                binding.article= item1
                binding.clickListener =clickListener
                binding.archiveListener=archiveListener
                binding.executePendingBindings()

            }else{
                binding.article= item
                binding.clickListener =clickListener
                binding.archiveListener=archiveListener
                binding.executePendingBindings()
            }

    binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
        override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
            youTubePlayer = initializedYouTubePlayer
        }
    })


        }
        fun cueVideo(urlsrc: String) {


            val link = urlsrc
            val url = link.split("v=".toRegex()).dropLastWhile { it.isEmpty()
            }.toTypedArray().get(1)

            currentVideoId = url

            if (youTubePlayer == null) {
                //return
                binding.youtubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady(initializedYouTubePlayer: YouTubePlayer) {
                        youTubePlayer = initializedYouTubePlayer
                        youTubePlayer!!.cueVideo(url, 0f)
                    }
                })
            }else{
                youTubePlayer!!.cueVideo(url, 0f)
            }


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

class ArchiveListener(val clickListener: (article: Article) -> Unit){
    fun onClick(article: Article )= clickListener(article)
}



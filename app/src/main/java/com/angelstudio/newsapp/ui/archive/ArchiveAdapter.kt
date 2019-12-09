package com.angelstudio.newsapp.ui.archive

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.databinding.ArchiveItemBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

class ArchiveAdapter(private val clickListener: ArchiveListener, private val archiveListener : ArchivearchiveListener, private val shareListener : ShareListener, val  lifecycle: Lifecycle, private val cxt : Context) :ListAdapter<Article,ArchiveAdapter.ArchiveViewHolder>(ArchiveDiffCallback()) {

    private val preferences: SharedPreferences
        get() = androidx.preference.PreferenceManager.getDefaultSharedPreferences(cxt)

    override fun onBindViewHolder(holder: ArchiveViewHolder, position: Int) {

        val country =preferences.getString("Country","")

        holder.bind(getItem(position),clickListener,archiveListener,shareListener,country)

        if(  getItem(position)?.source?.name.equals("Youtube.com")){
            getItem(position).url?.let {
                holder.cueVideo(it) }
        }

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding = ArchiveItemBinding.inflate(layoutInflater,parent,false)

        lifecycle.addObserver(binding.youtubePlayerView)

        return ArchiveViewHolder(binding)
    }

    class ArchiveViewHolder(val binding: ArchiveItemBinding): RecyclerView.ViewHolder(binding.root){
        private var currentVideoId: String? = null
        private var youTubePlayer: YouTubePlayer? = null

        fun bind(item: Article, clickListener: ArchiveListener,archiveListener:ArchivearchiveListener,shareListener: ShareListener,country : String?){


                binding.article= item
                binding.clickListener =clickListener
                binding.archiveListener=archiveListener
                binding.shareListener=shareListener
                binding.executePendingBindings()


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

class ArchiveDiffCallback :DiffUtil.ItemCallback<Article>(){
    override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem.title == newItem.title
    }
    override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
        return oldItem == newItem
    }
}

class ArchiveListener(val clickListener: (url: String,source :String) -> Unit){
    fun onClick(article: Article )= clickListener(article.url!!,article.source.name!!)
}

class ArchivearchiveListener(val clickListener: (article: Article) -> Unit){
    fun onClick(article: Article )= clickListener(article)
}

class ShareListener(val clickListener: (article: Article) -> Unit){
    fun onClick(article: Article )= clickListener(article)
}


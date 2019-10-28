package com.angelstudio.newsapp.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.databinding.TopHeadlineItemBinding

class TopHeadlineAdapter(val clickListener: TopHeadlineListener) :ListAdapter<Article,TopHeadlineAdapter.TopHeadlineViewHolder>(TopHeadlineDiffCallback()) {

    override fun onBindViewHolder(holder: TopHeadlineViewHolder, position: Int) {
                holder.bind(getItem(position),clickListener)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopHeadlineViewHolder {
        val layoutInflater=LayoutInflater.from(parent.context)
        val binding = TopHeadlineItemBinding.inflate(layoutInflater,parent,false)
        return TopHeadlineViewHolder(binding)
    }
    class TopHeadlineViewHolder(val binding: TopHeadlineItemBinding): RecyclerView.ViewHolder(binding.root){

        fun bind(item: Article, clickListener: TopHeadlineListener){
            binding.article= item
            binding.clickListener =clickListener
            binding.executePendingBindings()
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

class TopHeadlineListener(val clickListener: (url: String) -> Unit){
    fun onClick(article: Article )= clickListener(article.url!!)


}



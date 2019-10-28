package com.angelstudio.newsapp.internal

import android.content.Context
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.angelstudio.newsapp.R
import com.angelstudio.newsapp.data.db.entity.Article
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.layout_demo_grid.view.*


@BindingAdapter("title")
fun TextView.setTitle(item: Article?) {
    item?.let {
        text = it.title
    }
}

@BindingAdapter("content")
fun TextView.setContent(item: Article?) {
    item?.let {
        text = it.content
    }
}

@BindingAdapter("sourcename")
fun TextView.setSourceName(item: Article?) {
    item?.let {
        text = it.source.name
    }
}

@BindingAdapter("publishedAt")
fun TextView.setPublishedAt(item: Article?) {
    item?.let {
        text = it.publishedAt
    }
}

@BindingAdapter("author")
fun TextView.setAuthor(item: Article?) {
    item?.let {
        text = it.author
    }
}



@BindingAdapter("urlToImage")
fun ImageView.setImage(item: Article?) {

    val requestOptions = com.bumptech.glide.request.RequestOptions()
        .placeholder(R.drawable.ic_launcher_background)
        .error(R.drawable.ic_launcher_background)

    Glide.with(context)
        .applyDefaultRequestOptions(requestOptions)
        .load(item?.urlToImage)
        .into(this)



}


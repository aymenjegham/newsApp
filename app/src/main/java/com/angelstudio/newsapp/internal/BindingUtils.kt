package com.angelstudio.newsapp.internal

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.angelstudio.newsapp.data.db.entity.Article
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.LocalDateTime



@BindingAdapter("title")
fun TextView.setTitle(item: Article?) {
    item?.let {
        text = it.title
    }
}



@BindingAdapter("content")
fun TextView.setContent(item: Article?) {
    item?.let {
        text = edit(it.content)
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
        val DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val ld = LocalDateTime.parse(it.publishedAt, DATEFORMATTER)
        val tFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)

         text = ld.format(tFormatter)

    }
}

@BindingAdapter("publishedAtDay")
fun TextView.setPublishedAtDay(item: Article?) {
    item?.let {
        val DATEFORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val ld = LocalDateTime.parse(it.publishedAt, DATEFORMATTER)
        val dtFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        text = ld.format(dtFormatter)
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

    if(item?.source?.name.equals("Youtube.com")){
        visibility=View.GONE
    }else{
        visibility=View.VISIBLE
    }

    val requestOptions = com.bumptech.glide.request.RequestOptions()
        //.placeholder(R.drawable.radio_launcher_round)
        //.error(R.drawable.radio_launcher_round)

    Glide.with(context)
        .applyDefaultRequestOptions(requestOptions)
        .load(item?.urlToImage)
        .listener(object : RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                visibility=View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                return false
            }

        })
        .into(this)
}

@BindingAdapter("urlvideo")
fun YouTubePlayerView.loadVideo(item: Article?) {
    visibility = if(item?.source?.name.equals("Youtube.com")){
        View.VISIBLE
    }else{
        View.GONE
    }

}


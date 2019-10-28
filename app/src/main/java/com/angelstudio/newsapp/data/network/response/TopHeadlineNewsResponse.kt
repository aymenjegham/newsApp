package com.angelstudio.newsapp.data.network.response


import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.angelstudio.newsapp.data.db.entity.Article


data class TopHeadlineNewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)
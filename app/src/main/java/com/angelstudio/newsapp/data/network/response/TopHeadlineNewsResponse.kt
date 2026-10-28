package com.angelstudio.newsapp.data.network.response


data class TopHeadlineNewsResponse(
    val articles: List<NewsArticleResponse>,
    val status: String,
    val totalResults: Int
)

data class NewsArticleResponse(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: NewsSourceResponse?,
    val title: String?,
    val url: String?,
    val urlToImage: String?
)

data class NewsSourceResponse(
    val id: String?,
    val name: String?
)
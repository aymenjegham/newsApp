package com.angelstudio.newsapp.domain.model

data class NewsArticle(
    val id: Int?,
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val sourceId: String?,
    val sourceName: String?,
    val title: String?,
    val url: String?,
    val imageUrl: String?
) {
    val stableKey: String
        get() = url ?: id?.let { "article-$it" }
            ?: listOf(sourceId, sourceName, title, publishedAt).joinToString("|")
}

data class SavedArticle(
    val article: NewsArticle,
    val isRead: Boolean,
    val savedAt: Long
)

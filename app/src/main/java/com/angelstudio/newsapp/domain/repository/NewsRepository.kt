package com.angelstudio.newsapp.domain.repository

import com.angelstudio.newsapp.domain.model.NewsArticle
import com.angelstudio.newsapp.domain.model.SavedArticle
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    val headlines: Flow<List<NewsArticle>>
    val savedArticles: Flow<List<SavedArticle>>

    suspend fun refresh(categories: Set<String>, country: String): Result<Unit>
    suspend fun save(article: NewsArticle)
    suspend fun removeSaved(article: NewsArticle)
    suspend fun markSavedArticleRead(url: String)
    suspend fun deleteAllSaved()
}

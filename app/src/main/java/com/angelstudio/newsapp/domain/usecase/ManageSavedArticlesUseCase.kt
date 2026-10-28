package com.angelstudio.newsapp.domain.usecase

import com.angelstudio.newsapp.domain.model.NewsArticle
import com.angelstudio.newsapp.domain.repository.NewsRepository
import javax.inject.Inject

class ManageSavedArticlesUseCase @Inject constructor(
    private val repository: NewsRepository
) {
    suspend fun save(article: NewsArticle) = repository.save(article)
    suspend fun remove(article: NewsArticle) = repository.removeSaved(article)
    suspend fun markRead(url: String) = repository.markSavedArticleRead(url)
    suspend fun deleteAll() = repository.deleteAllSaved()
}

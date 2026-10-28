package com.angelstudio.newsapp.data.repository

import android.util.Log
import com.angelstudio.newsapp.data.db.TopHeadlineDao
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article as ArticleEntity
import com.angelstudio.newsapp.data.db.entity.Source
import com.angelstudio.newsapp.data.network.NewsApiService
import com.angelstudio.newsapp.domain.model.NewsArticle
import com.angelstudio.newsapp.domain.model.SavedArticle
import com.angelstudio.newsapp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val topHeadlineDao: TopHeadlineDao
) : NewsRepository {

    override val headlines: Flow<List<NewsArticle>> =
        topHeadlineDao.observeTopHeadlines().map { articles -> articles.map { it.toDomain() } }

    override val savedArticles: Flow<List<SavedArticle>> =
        topHeadlineDao.observeSavedArticles().map { articles ->
            articles.map { saved ->
                SavedArticle(saved.toDomain(), saved.isRead, saved.savedAt)
            }
        }

    override suspend fun refresh(categories: Set<String>, country: String): Result<Unit> {
        if (categories.isEmpty()) {
            return Result.failure(IllegalArgumentException("Select at least one news category."))
        }

        return try {
            val articles = categories.flatMap { category ->
                newsApiService.getTopHeadlines(category, country, PAGE_SIZE).articles
            }
                .distinctBy { it.url ?: it.title }
                .map { response ->
                    Log.v("checkresult", response.toString())
                    ArticleEntity(
                        author = response.author,
                        content = response.content,
                        description = response.description,
                        publishedAt = response.publishedAt,
                        source = Source(response.source?.id, response.source?.name),
                        title = response.title,
                        url = response.url,
                        urlToImage = response.urlToImage
                    )
                }

            topHeadlineDao.replaceTopHeadlines(articles)
            Result.success(Unit)
        } catch (exception: IOException) {
            Result.failure(exception)
        } catch (exception: HttpException) {
            Result.failure(exception)
        }
    }

    override suspend fun save(article: NewsArticle) {
        topHeadlineDao.archive(article.toArchive())
    }

    override suspend fun removeSaved(article: NewsArticle) {
        article.url?.let { topHeadlineDao.deleteSavedByUrl(it) }
    }

    override suspend fun markSavedArticleRead(url: String) {
        topHeadlineDao.markSavedArticleRead(url)
    }

    override suspend fun deleteAllSaved() {
        topHeadlineDao.deleteAll()
    }

    private fun ArticleEntity.toDomain() = NewsArticle(
        id = id,
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
        sourceId = source.id,
        sourceName = source.name,
        title = title,
        url = url,
        imageUrl = urlToImage
    )

    private fun Archive.toDomain() = NewsArticle(
        id = id,
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
        sourceId = source.id,
        sourceName = source.name,
        title = title,
        url = url,
        imageUrl = urlToImage
    )

    private fun NewsArticle.toArchive() = Archive(
        author = author,
        content = content,
        description = description,
        publishedAt = publishedAt,
        source = Source(sourceId, sourceName),
        title = title,
        url = url,
        urlToImage = imageUrl,
        savedAt = System.currentTimeMillis()
    )

    private companion object {
        const val PAGE_SIZE = "100"
    }
}

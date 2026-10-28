package com.angelstudio.newsapp.data.db

import androidx.room.*
import com.angelstudio.newsapp.data.db.entity.Archive
import com.angelstudio.newsapp.data.db.entity.Article
import kotlinx.coroutines.flow.Flow

@Dao
interface TopHeadlineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(articles: List<Article>)

    @Query("select * from TopHeadline ")
    fun observeTopHeadlines(): Flow<List<Article>>

    @Query("delete from TopHeadline")
    suspend fun deleteOldTopHeadline()

    @Transaction
    suspend fun replaceTopHeadlines(articles: List<Article>) {
        deleteOldTopHeadline()
        if (articles.isNotEmpty()) insert(articles)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun archive(archive: Archive)

    @Query("DELETE FROM Archive WHERE url = :url")
    suspend fun deleteSavedByUrl(url: String)

    @Query("select * from Archive ")
    fun observeSavedArticles(): Flow<List<Archive>>

    @Query("UPDATE Archive SET isRead = 1 WHERE url = :url")
    suspend fun markSavedArticleRead(url: String)

    @Query("DELETE FROM Archive")
    suspend fun deleteAll()
}
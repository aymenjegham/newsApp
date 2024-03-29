package com.angelstudio.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse

interface TopHeadlineRepository {

    suspend fun getTopHeadline(): LiveData<List<Article>>
    suspend fun getConnectivity(): LiveData<Boolean>
    suspend fun archive(article: Article)
    suspend fun getArchive(): LiveData<List<Article>>
    suspend fun desarchive(article: Article)
    suspend fun deleteAll()






    suspend fun fetch()

}
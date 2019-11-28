package com.angelstudio.newsapp.data.repository

import androidx.lifecycle.LiveData
import com.angelstudio.newsapp.data.db.entity.Article
import com.angelstudio.newsapp.data.network.response.TopHeadlineNewsResponse

interface TopHeadlineRepository {

    suspend fun getTopHeadline(): LiveData<List<Article>>
    suspend fun getConnectivity(): LiveData<Boolean>


    suspend fun fetch()

}